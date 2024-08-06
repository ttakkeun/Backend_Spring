//package ttakkeun.ttakkeun_server.service;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import ttakkeun.ttakkeun_server.converter.ProductConverter;
//import ttakkeun.ttakkeun_server.dto.diagnose.ProductDTO;
//import ttakkeun.ttakkeun_server.entity.Product;
//import ttakkeun.ttakkeun_server.repository.ProductRepository;
//import ttakkeun.ttakkeun_server.repository.ResultRepository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class ProductService {
//    private final ProductRepository productRepository;
//    private final ResultRepository resultRepository;
//    private final LikeService likeService;
//    private final ProductConverter productConverter;
//
//    //진단의 ai추천제품
//    public List<ProductDTO> getResultProducts() {
//        Long latestResultId = resultRepository.findLatestResultId();
//        List<Product> products = productRepository.findByResultId(latestResultId);
//
//        return products.stream().map(productConverter::toDTO).collect(Collectors.toList());
//    }
//}
