package code.project.service;

public interface HospitalListView {
    Long getHospitalId();
    String getHospitalName();
    boolean isHasEmergency();
    Double getLatitude();
    Double getLongitude();
    String getAddress();
}