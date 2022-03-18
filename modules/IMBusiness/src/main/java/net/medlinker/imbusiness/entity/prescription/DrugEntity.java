package net.medlinker.imbusiness.entity.prescription;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/3/9
 */
public class DrugEntity implements Parcelable {

    private long id;

    private String drugId;

    private int platform;

    private String title;

    private String subTitle;

    private List<String> images;

    private List<String> tags;

    private int type;

    private String typeText;

    private String supplier;

    private String note;

    private String manufacturer;
    private String brand;
    private String specification;
    private String attending;
    private String adverseReaction;
    private String taboo;
    private String drugInteract;
    private String usage;
    private String dosage;
    private String doctorAdvice;
    private int originPrice;
    private int finalPrice;
    private int quantity;
    private int totalPrice;

    //
    private String name;
    private String genericName;

    private String medicineClinicId;
    private String thirdId;
    private String drugName;
    private long drugNum;
    private int stock;
    private int otcType = -1;
    private String tradeName;
    private long spuId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getAttending() {
        return attending;
    }

    public void setAttending(String attending) {
        this.attending = attending;
    }

    public String getAdverseReaction() {
        return adverseReaction;
    }

    public void setAdverseReaction(String adverseReaction) {
        this.adverseReaction = adverseReaction;
    }

    public String getTaboo() {
        return taboo;
    }

    public void setTaboo(String taboo) {
        this.taboo = taboo;
    }

    public String getDrugInteract() {
        return drugInteract;
    }

    public void setDrugInteract(String drugInteract) {
        this.drugInteract = drugInteract;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorAdvice() {
        return doctorAdvice;
    }

    public void setDoctorAdvice(String doctorAdvice) {
        this.doctorAdvice = doctorAdvice;
    }

    public int getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(int originPrice) {
        this.originPrice = originPrice;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getMedicineClinicId() {
        return medicineClinicId;
    }

    public void setMedicineClinicId(String medicineClinicId) {
        this.medicineClinicId = medicineClinicId;
    }

    public String getThirdId() {
        return thirdId;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public long getDrugNum() {
        return drugNum;
    }

    public void setDrugNum(long drugNum) {
        this.drugNum = drugNum;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getOtcType() {
        return otcType;
    }

    public void setOtcType(int otcType) {
        this.otcType = otcType;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public long getSpuId() {
        return spuId;
    }

    public void setSpuId(long spuId) {
        this.spuId = spuId;
    }

    @Override
    public String toString() {
        return "DrugEntity{" +
                "drugId=" + drugId +
                ", platform=" + platform +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", images=" + images +
                ", tags=" + tags +
                ", type=" + type +
                ", typeText='" + typeText + '\'' +
                ", supplier='" + supplier + '\'' +
                ", note='" + note + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", brand='" + brand + '\'' +
                ", specification='" + specification + '\'' +
                ", attending='" + attending + '\'' +
                ", adverseReaction='" + adverseReaction + '\'' +
                ", taboo='" + taboo + '\'' +
                ", drugInteract='" + drugInteract + '\'' +
                ", usage='" + usage + '\'' +
                ", dosage='" + dosage + '\'' +
                ", doctorAdvice='" + doctorAdvice + '\'' +
                ", originPrice=" + originPrice +
                ", finalPrice=" + finalPrice +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.drugId);
        dest.writeInt(this.platform);
        dest.writeString(this.title);
        dest.writeString(this.subTitle);
        dest.writeStringList(this.images);
        dest.writeStringList(this.tags);
        dest.writeInt(this.type);
        dest.writeString(this.typeText);
        dest.writeString(this.supplier);
        dest.writeString(this.note);
        dest.writeString(this.manufacturer);
        dest.writeString(this.brand);
        dest.writeString(this.specification);
        dest.writeString(this.attending);
        dest.writeString(this.adverseReaction);
        dest.writeString(this.taboo);
        dest.writeString(this.drugInteract);
        dest.writeString(this.usage);
        dest.writeString(this.dosage);
        dest.writeString(this.doctorAdvice);
        dest.writeInt(this.originPrice);
        dest.writeInt(this.finalPrice);
        dest.writeInt(this.quantity);
        dest.writeInt(this.totalPrice);
        dest.writeString(this.name);
        dest.writeString(this.genericName);
        dest.writeString(this.medicineClinicId);
        dest.writeString(this.thirdId);
        dest.writeString(this.drugName);
        dest.writeLong(this.drugNum);
        dest.writeInt(this.stock);
    }

    public DrugEntity() {
    }

    protected DrugEntity(Parcel in) {
        this.id = in.readLong();
        this.drugId = in.readString();
        this.platform = in.readInt();
        this.title = in.readString();
        this.subTitle = in.readString();
        this.images = in.createStringArrayList();
        this.tags = in.createStringArrayList();
        this.type = in.readInt();
        this.typeText = in.readString();
        this.supplier = in.readString();
        this.note = in.readString();
        this.manufacturer = in.readString();
        this.brand = in.readString();
        this.specification = in.readString();
        this.attending = in.readString();
        this.adverseReaction = in.readString();
        this.taboo = in.readString();
        this.drugInteract = in.readString();
        this.usage = in.readString();
        this.dosage = in.readString();
        this.doctorAdvice = in.readString();
        this.originPrice = in.readInt();
        this.finalPrice = in.readInt();
        this.quantity = in.readInt();
        this.totalPrice = in.readInt();
        this.name = in.readString();
        this.genericName = in.readString();
        this.medicineClinicId = in.readString();
        this.thirdId = in.readString();
        this.drugName = in.readString();
        this.drugNum = in.readLong();
        this.stock = in.readInt();
    }

    public static final Creator<DrugEntity> CREATOR = new Creator<DrugEntity>() {
        @Override
        public DrugEntity createFromParcel(Parcel source) {
            return new DrugEntity(source);
        }

        @Override
        public DrugEntity[] newArray(int size) {
            return new DrugEntity[size];
        }
    };
}
