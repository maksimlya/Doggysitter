package afeka.com.doggysitter.ListViews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Dog {
    private String name;
    private Bitmap photo;

    public Dog(){
    }

    public Dog(String name, Bitmap photo){
        this.name = name;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(String photoLoc) {
        this.photo = BitmapFactory.decodeFile(photoLoc);
    }
}
