package ttakkeun.ttakkeun_server.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ttakkeun.ttakkeun_server.dto.diagnose.NaverProductDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.UpdateProductsRequestDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.UpdateProductsResponseDTO;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.entity.Result;
import ttakkeun.ttakkeun_server.entity.ResultProduct;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.ProductRepository;
import ttakkeun.ttakkeun_server.repository.ResultProductRepository;
import ttakkeun.ttakkeun_server.repository.ResultRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class DiagnoseNaverProductService {

    private final ResultRepository resultRepository;
    private final ProductRepository productRepository;
    private final ResultProductRepository resultProductRepository;

    private final String naverClientId;
    private final String naverClientSecret;

    @Autowired
    public DiagnoseNaverProductService(ResultRepository resultRepository, ProductRepository productRepository, ResultProductRepository resultProductRepository,
                           @Value("${NAVER_ClientID}") String naverClientId,
                           @Value("${NAVER_ClientSecret}") String naverClientSecret) {
        this.resultRepository = resultRepository;
        this.productRepository = productRepository;
        this.resultProductRepository = resultProductRepository;
        this.naverClientId = naverClientId;
        this.naverClientSecret = naverClientSecret;
    }

    // 네이버 쇼핑 API로 DB 정보 업데이트
    public UpdateProductsResponseDTO updateDiagnoseProducts(Long diagnoseId, UpdateProductsRequestDTO updateProductsRequestDTO) {
        Optional<Result> resultOpt = resultRepository.findByResultId(diagnoseId);

        try {
            if (resultOpt.isPresent()) {
                Result result = resultOpt.get(); // result 가져옴
                Category resultCategory = result.getResultCategory(); // 태그값은 추후 제품 정보를 저장할 때도 필요하므로
                List<NaverProductDTO> products = updateProductsRequestDTO.products().stream()
                        .map(productTitle -> {
                            System.out.println("product is : " + productTitle);
                            NaverProductDTO naverShopSearch = naverShopSearch(productTitle, resultCategory);

                            return naverShopSearch;
//                            Optional<Product> productOpt = productRepository.findByProductTitle(productTitle);
//                            // productOpt 값이 비어있는걸로 나옴!!!! 해당 부분 수정 중
//                            System.out.println("productOpt is : " + productOpt);
//                            if (productOpt.isPresent()) { // ResultProduct 테이블에 다대다 매핑 추가
//                                ResultProduct resultProduct = new ResultProduct();
//                                Product product = productOpt.get();
//                                resultProduct.setProduct(product);
//                                resultProduct.setResult(result);
//                                resultProductRepository.save(resultProduct);
//                                return naverShopSearch;
//                            } else {
//                                throw new NoSuchElementException("제품을 찾을 수 없습니다. 다시 시도해주세요");
//                            }
                        })
                        .collect(Collectors.toList());

                return new UpdateProductsResponseDTO(products);
            } else {
                // Optional 객체에서 값이 비어있는 경우 예외 발생
                throw new NoSuchElementException("진단 결과를 찾을 수 없습니다. 다시 시도해주세요");
            }
        } catch (NoSuchElementException e) {
            log.error("DB에서 값을 찾을 수 없습니다.", e);
            throw new RuntimeException("DB에서 값을 찾을 수 없습니다. 서버 관리자에게 문의해주세요"  + e.getMessage());
        } catch (Exception e) {
            log.error("DB 정보 업데이트에 실패했습니다.", e);
            throw new RuntimeException("DB 정보 업데이트에 실패했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }

    // 네이버 쇼핑 API 사용
    public NaverProductDTO naverShopSearch(String productTitle, Category resultCategory) {
        System.out.println("ProductTitle is : " + productTitle);
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", naverClientId);
        headers.add("X-Naver-Client-Secret", naverClientSecret);
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?query=" + productTitle, HttpMethod.GET, requestEntity, String.class);
        HttpStatusCode httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        try {
            if (status == 200) {// 네이버 쇼핑 API와 정상적으로 통신하였을 경우
                System.out.println("Response status: " + status);
                System.out.println(response);
                NaverProductDTO naverResponse = fromJSONToNaverProducts(response, resultCategory);
                return naverResponse;
            } else { // status가 200이 아닐 경우, 즉 네이버 쇼핑 API와 통신에 실패하였을 경우
                throw new RuntimeException("네이버 쇼핑 API 호출 실패 : " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            log.error("네이버 쇼핑 API 호출에 실패했습니다.", e);
            throw new RuntimeException("네이버 쇼핑 API 호출에 실패했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }

    // naverShopSearch에서 받은 JSON을 파싱해서 NaverProductDTO 객체 리스트로 변환함
    public NaverProductDTO fromJSONToNaverProducts(String result, Category resultCategory) {
            try {
                JSONObject rjson = new JSONObject(result);
                JSONArray items  = rjson.getJSONArray("items");

                if (items.length() == 0) { // 네이버 쇼핑 API 결과에 제품이 존재하지 않을 경우
                    // 네이버 쇼핑 API는 유사도로 검색을 하기 때문에 items에 값이 없는 경우에는 예외로 처리하였음
                    throw new RuntimeException("검색 결과가 없습니다. 다시 시도해주세요");
                }

                JSONObject itemJson = items.getJSONObject(0); // 유사도가 가장 높은 제품, 즉 첫 번째 제품을 사용
                System.out.println("itemJson : " + itemJson);
                String productIdStr = itemJson.getString("productId");
                Long productId = Long.parseLong(productIdStr);
                String title = itemJson.getString("title");
                String link = itemJson.getString("link");
                String image = itemJson.getString("image");
                String lpriceStr = itemJson.getString("lprice");
                Integer lprice = Integer.parseInt(lpriceStr);
                String mall_name = itemJson.getString("mallName");
                String brand = itemJson.getString("brand");
                String category1 = itemJson.getString("category1");
                String category2 = itemJson.getString("category2");
                String category3 = itemJson.getString("category3");
                String category4 = itemJson.getString("category4");

                // Json에서 가져온 값으로 DTO build
                NaverProductDTO naverProductDTO = NaverProductDTO.builder()
                        .productId(productId)
                        .title(title)
                        .link(link)
                        .image(image)
                        .lprice(lprice)
                        .mall_name(mall_name)
                        .brand(brand)
                        .category1(category1)
                        .category2(category2)
                        .category3(category3)
                        .category4(category4)
                        .tag(resultCategory)
                        .build();

                // 만약 productId가 db에 존재하지 않을 경우 saveProduct
                if (!productRepository.existsByproductId(productId)) {
                    saveProduct(naverProductDTO);
                } else {
                    productRepository.findById(productId).ifPresent(product -> {
                        if (product.getTag() == null) {
                            // product값은 db에 존재하나 해당 product값에 tag값이 존재하지 않을 경우
                            // 검색 결과를 통해 저장한 제품은 자체적으로 분류한 태그값이 들어가있지 않기 때문에 태그값을 저장해줘야 함
                            product.setTag(resultCategory);  // tag값 설정
                            productRepository.save(product);  // tag값만 설정한 뒤 저장함
                        }
                    }); // Optional 객체를 사용하고 있으므로 map과 ifPresent를 사용하도록 수정하였음
                // 이외의 경우 이전에 진단을 받은 제품이거나 자체적으로 삽입한 제품값들이기 때문에 별도의 저장 과정을 거치지 않아도 됨
                }

            return naverProductDTO;
        } catch (JSONException e) {
            log.error("JSON 파싱 중 오류가 발생했습니다.", e);
            throw new RuntimeException("JSON 파싱 중 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (NumberFormatException e) {
            log.error("숫자 형식 변환 오류가 발생했습니다.", e);
            throw new RuntimeException("숫자 형식 변환 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (Exception e) {
            log.error("알 수 없는 오류가 발생했습니다.", e);
            throw new RuntimeException("알 수 없는 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }

    @Transactional
    public Product saveProduct(NaverProductDTO naverProductDTO) {
        // product db에 값을 저장함
        Product product = new Product();
        product.setProductId(naverProductDTO.productId());
        product.setProductTitle(naverProductDTO.title());
        product.setProductLink(naverProductDTO.link());
        product.setProductImage(naverProductDTO.image());
        product.setLprice(naverProductDTO.lprice());
        product.setBrand(naverProductDTO.brand());
        product.setCategory1(naverProductDTO.category1());
        product.setCategory2(naverProductDTO.category2());
        product.setCategory3(naverProductDTO.category3());
        product.setCategory4(naverProductDTO.category4());
        product.setTag(naverProductDTO.tag());
        return productRepository.save(product);
    }

}
