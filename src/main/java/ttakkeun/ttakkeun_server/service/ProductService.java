package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.converter.ProductConverter;
import ttakkeun.ttakkeun_server.dto.ProductDTO;
import ttakkeun.ttakkeun_server.entity.Product;
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
    public List<ProductDTO> getResultProducts() {
        Long latestResultId = resultRepository.findLatestResultId();
        List<Product> products = productRepository.findByResultId(latestResultId);

        return products.stream().map(productConverter::toDTO).collect(Collectors.toList());
    }

    //랭킹별 추천제품(한 페이지당 20개)
    public List<ProductDTO> getRankedProducts(int page) {
        List<Product> products = productRepository.sortedByLikesWithPaging(page, 20);

        return products.stream().map(productConverter::toDTO).collect(Collectors.toList());
    }

    //부위 별 랭킹(한 페이지당 20개)
    public List<ProductDTO> getTagRankingProducts(String tag, int page) {
        List<Product> products = productRepository.findByTag(tag, page, 20);

        return products.stream().map(productConverter::toDTO).collect(Collectors.toList());
    }
}
