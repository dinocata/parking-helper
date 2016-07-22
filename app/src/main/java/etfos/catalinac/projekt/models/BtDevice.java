package etfos.catalinac.projekt.models;

public class BtDevice {

    private String name;
    private String address;
    private int imgRes;

    public BtDevice(String name, String address, int imgRes) {
        this.address = address;
        this.name = name;
        this.imgRes = imgRes;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getImgRes() {
        return imgRes;
    }

}
