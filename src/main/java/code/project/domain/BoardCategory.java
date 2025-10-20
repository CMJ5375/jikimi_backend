package code.project.domain;

public enum BoardCategory {
    HOSPITAL_INFO("병원정보"),
    PHARMACY_INFO("약국정보"),
    QUESTION("질문해요"),
    FREE("자유글");

    private final String displayName;

    BoardCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
