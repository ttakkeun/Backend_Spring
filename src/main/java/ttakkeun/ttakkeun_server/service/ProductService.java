package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.converter.ProductConverter;
import ttakkeun.ttakkeun_server.dto.RecommendProductDTO;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.ProductRepository;
import ttakkeun.ttakkeun_server.repository.ResultRepository;

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
}
