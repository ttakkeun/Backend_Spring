package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ReportImage {
	@Id
	@Column(name = "report_image_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id")
	private ReportTip reportTip;
}