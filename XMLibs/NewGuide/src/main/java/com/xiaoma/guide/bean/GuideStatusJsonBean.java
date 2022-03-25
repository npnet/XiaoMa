package com.xiaoma.guide.bean;

public class GuideStatusJsonBean {
    private Xting xting;
    private Music music;
    private Personal personal;
    private Service service;
    private AppStore appStore;
    private Launcher launcher;
    private Club club;
    private Shop shop;
    private CarPark carPark;
    private Pet pet;

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Xting getXting() {
        return xting;
    }

    public void setXting(Xting xting) {
        this.xting = xting;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public AppStore getAppStore() {
        return appStore;
    }

    public void setAppStore(AppStore appStore) {
        this.appStore = appStore;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public CarPark getCarPark() {
        return carPark;
    }

    public void setCarPark(CarPark carPark) {
        this.carPark = carPark;
    }

    public static class Xting extends BaseGuideStatusBean {
    }

    public static class Music extends BaseGuideStatusBean {
    }

    public static class Personal extends BaseGuideStatusBean {
    }

    public static class Service extends BaseGuideStatusBean {
    }

    public static class AppStore extends BaseGuideStatusBean {
    }

    public static class Shop extends BaseGuideStatusBean {
    }

    public static class Club extends BaseGuideStatusBean {
    }

    public static class Launcher extends BaseGuideStatusBean {
    }

    public static class CarPark extends BaseGuideStatusBean {
    }

    public static class Pet extends BaseGuideStatusBean {
    }

}
