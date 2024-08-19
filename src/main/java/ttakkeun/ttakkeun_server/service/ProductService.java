package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.converter.ProductConverter;
import ttakkeun.ttakkeun_server.dto.RecommendProductDTO;
import ttakkeun.ttakkeun_server.dto.product.ProductRequestDTO;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.ProductRepository;
import ttakkeun.ttakkeun_server.repository.RecordRepository;
import ttakkeun.ttakkeun_server.repository.ResultRepository;
import ttakkeun.ttakkeun_server.utils.NaverShopSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ResultRepository resultRepository;
    private final LikeService likeService;
    private final ProductConverter productConverter;
    private final NaverShopSearch naverShopSearch;
    private final RecordRepository recordRepository;

    //진단의 ai추천제품
    public List<RecommendProductDTO> getResultProducts(Long petId, Long memberId) {
        //펫 -> 일지 -> 가장 최근 진단 -> 진단 추천 제품
        List<Record> records = recordRepository.findByPet_PetId(petId);
        Long latestResultId = resultRepository.findLatestResultId(records);
        List<Product> products = productRepository.findByResultId(latestResultId);

        return products.stream()
                .map(product -> productConverter.toDTO(product, memberId))
                .collect(Collectors.toList());
    }

    //랭킹별 추천제품(한 페이지당 20개)
    public List<RecommendProductDTO> getRankedProducts(int page, Long memberId) {
        Pageable pageable = PageRequest.of(page, 20);
        List<Product> products = productRepository.sortedByLikesWithPaging(pageable).getContent();

        return products.stream()
                .map(product -> productConverter.toDTO(product, memberId))
                .collect(Collectors.toList());
    }

    //부위 별 랭킹(한 페이지당 20개)
    public List<RecommendProductDTO> getTagRankingProducts(Category tag, int page, Long memberId) {
        Pageable pageable = PageRequest.of(page, 20);
        List<Product> products = productRepository.findByTag(tag, pageable).getContent();

        return products.stream()
                .map(product -> productConverter.toDTO(product, memberId))
                .collect(Collectors.toList());
    }

    //새로운 제품을 좋아요 했을 경우 Product DB에 추가
    public void addNewProduct(Long productId, ProductRequestDTO requestDTO) {
        Product product = productConverter.toProduct(productId, requestDTO);

        if(productRepository.findById(productId).isEmpty()) {
            productRepository.save(product);
        }
    }

    //검색 기능 - 따끈 DB에서 불러오기
    public List<RecommendProductDTO> getProductByKeywordFromDB(String keyword, int page, Long memberId) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Product> products = productRepository.findByProductTitle(keyword, pageable).getContent();

        return products.stream()
                .map(product -> productConverter.toDTO(product, memberId))
                .collect(Collectors.toList());
    }

    //검색 기능 - 네이버 쇼핑에서 불러오기
    public List<RecommendProductDTO> getProductByKeywordFromNaver(String keyword, Long memberId) {

        List<RecommendProductDTO> productDTOs = new ArrayList<>();

        //검색 키워드 앞에 반려동물을 붙여서 검색 ex) 샴푸 검색 -> 반려동물 샴푸 검색
        //총 10개의 알맞은 제품을 가지고 오거나 총 100개의 제품까지 탐색했을 때까지 반복
        for(int page = 0; productDTOs.size() < 10 && page < 10; page++) {
            String naverString = naverShopSearch.search("반려동물 " + keyword, page);
            JSONObject jsonObject = new JSONObject(naverString);
            JSONArray jsonArray = jsonObject.getJSONArray("items");


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JSONProduct = (JSONObject) jsonArray.get(i);
                RecommendProductDTO productDTO = productConverter.JSONToDTO(JSONProduct, memberId);

                //제품이 알맞은 스킨케어 관련 카테고리를 가지고 있는지 확인
                if (productDTOs.size() < 10 && productConverter.categoryFilter(productDTO)) {
                    productDTOs.add(productDTO);
                }
            }
        }

        //키워드 그대로 검색 (검색어 앞에 반려동물을 붙이면 올바른 제품이 나오지 않을 때가 있음)
        //총 10개의 알맞은 제품을 가지고 오거나 총 100개의 제품까지 탐색했을 때까지 반복
        for(int page = 0; productDTOs.size() < 10 && page < 10; page++) {
            String naverString = naverShopSearch.search(keyword, page);
            JSONObject jsonObject = new JSONObject(naverString);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JSONProduct = (JSONObject) jsonArray.get(i);
                RecommendProductDTO productDTO = productConverter.JSONToDTO(JSONProduct, memberId);

                //제품이 알맞은 스킨케어 관련 카테고리를 가지고 있는지 확인
                if (productDTOs.size() < 10 && productConverter.categoryFilter(productDTO)) {
                    productDTOs.add(productDTO);
                }
            }
        }
        return productDTOs;
    }


}
