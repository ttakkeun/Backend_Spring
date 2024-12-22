package ttakkeun.ttakkeun_server.dto.tip;

public class PostTipReportRequestDTO {
	private Long tip_id;
	private String report_category;
	private String report_detail;

	// 기본 생성자
	public PostTipReportRequestDTO() {}

	// 생성자
	public PostTipReportRequestDTO(Long tip_id, String report_category, String report_detail) {
		this.tip_id = tip_id;
		this.report_category = report_category;
		this.report_detail = report_detail;
	}

	// Getter 및 Setter
	public Long getTip_id() {
		return tip_id;
	}

	public void setTip_id(Long tip_id) {
		this.tip_id = tip_id;
	}

	public String getReport_category() {
		return report_category;
	}

	public void setReport_category(String report_category) {
		this.report_category = report_category;
	}

	public String getReport_detail() {
		return report_detail;
	}

	public void setReport_detail(String report_detail) {
		this.report_detail = report_detail;
	}
}
