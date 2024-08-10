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
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.ProductRepository;
import ttakkeun.ttakkeun_server.repository.ResultRepository;
import ttakkeun.ttakkeun_server.utils.NaverShopSearch;

import java.util.ArrayList;
import java.util.List;
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

    //진단의 ai추천제품
    public List<RecommendProductDTO> getResultProducts() {
        Long latestResultId = resultRepository.findLatestResultId();
        List<Product> products = productRepository.findByResultId(latestResultId);

        return products.stream().map(productConverter::toDTO).collect(Collectors.toList());
    }

    //랭킹별 추천제품(한 페이지당 20개)
    public List<RecommendProductDTO> getRankedProducts(int page) {
        Pageable pageable = PageRequest.of(page, 20);
        List<Product> products = productRepository.sortedByLikesWithPaging(pageable).getContent();

        return products.stream().map(productConverter::toDTO).collect(Collectors.toList());
    }

    //부위 별 랭킹(한 페이지당 20개)
    public List<RecommendProductDTO> getTagRankingProducts(Category tag, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        List<Product> products = productRepository.findByTag(tag, pageable).getContent();

        return products.stream().map(productConverter::toDTO).collect(Collectors.toList());
    }

    //좋아요를 누른 새로운 제품을 저장
    public void saveNewProduct(RecommendProductDTO recommendProductDTO) {
        if (recommendProductDTO.getProduct_id() != null && productRepository.existsById(recommendProductDTO.getProduct_id())) {
            return;
        }
        Product product = productConverter.toEntity(recommendProductDTO);
        productRepository.save(product);
    }

    //검색 기능 - 따끈 DB에서 불러오기
    public List<RecommendProductDTO> getProductByKeywordFromDB(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Product> products = productRepository.findByProductTitle(keyword, pageable).getContent();

        return products.stream().map(productConverter::toDTO).collect(Collectors.toList());
    }
    //검색 기능 - 네이버 쇼핑에서 불러오기
    public List<RecommendProductDTO> getProductByKeywordFromNaver(String keyword) {
        //물품을 반려 동물 카테고리로 좁히기 위한 키워드 설정
        String petKeyword = "반려 동물" + keyword;

        String naverString = naverShopSearch.search(petKeyword);
        JSONObject jsonObject = new JSONObject(naverString);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        List<RecommendProductDTO> productDTOs = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject JSONProduct = (JSONObject) jsonArray.get(i);
            RecommendProductDTO productDTO = productConverter.JSONToDTO(JSONProduct);
            productDTOs.add(productDTO);
        }

        return productDTOs;
    }
}
