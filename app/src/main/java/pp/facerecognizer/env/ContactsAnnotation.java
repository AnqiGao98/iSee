package pp.facerecognizer.env;

public class ContactsAnnotation{
    private String contactName;
    private String contactAnnotation;
    private int imgId;

    public ContactsAnnotation(String name, int imgId) {
        this.contactName = name;
        this.imgId = imgId;
    }

//    public ContactsAnnotation(String name, String annotation, int imgId) {
//        this.contactName = name;
//        this.contactAnnotation = annotation;
//        this.imgId = imgId;
//    }
    public String getContactName() {
        return contactName;
    }
    public void setContactName(String name) {
        this.contactName = name;
    }

//    public String getContactAnnotation() {
//        return contactAnnotation;
//    }
//    public void setContactAnnotation(String annotation) {
//        this.contactAnnotation = annotation;
//    }

    public int getImgId() {
        return imgId;
    }
    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
