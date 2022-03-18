package com.medlinker.lib.imagepicker.entity;


import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/17 16:47
 */
public class PhotoDirectory {

    private String id;
    private String coverPath;
    private String name;
    private long dateAdded;
    private List<ImageEntity> photos = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoDirectory)) return false;

        PhotoDirectory that = (PhotoDirectory) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String path) {
        this.coverPath = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<ImageEntity> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ImageEntity> photos) {
        this.photos = photos;
    }

    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<>(photos.size());
        for (ImageEntity photo : photos) {
            paths.add(photo.getFilePath());
        }
        return paths;
    }

    public void addPhoto(String path) {
        photos.add(new ImageEntity(path));
    }
}
