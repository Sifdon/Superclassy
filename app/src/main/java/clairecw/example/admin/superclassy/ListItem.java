package clairecw.example.admin.superclassy;

/**
 * Created by admin on 4/20/16.
 */
public class ListItem {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    @Override
    public String toString() {
        return url;
    }
}