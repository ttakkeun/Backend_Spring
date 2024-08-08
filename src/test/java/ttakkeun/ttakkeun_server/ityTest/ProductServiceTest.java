package ttakkeun.ttakkeun_server.ityTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.repository.ProductRepository;
import ttakkeun.ttakkeun_server.repository.ResultRepository;
import ttakkeun.ttakkeun_server.service.LikeService;
import ttakkeun.ttakkeun_server.service.MemberService;
import ttakkeun.ttakkeun_server.service.ProductService;

@SpringBootTest
@Transactional
@WebMvcTest
public class ProductServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ResultRepository resultRepository;
    @Autowired
    LikeService likeService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void ai제품() throws Exception {
        Product product = productRepository.findById(1L);
        System.out.println(product);
    }
}
