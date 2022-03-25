package com.xiaoma.launcher.favorites;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/04/11
 *     desc   :
 * </pre>
 */
@Table("xiaoma_Poi_favorite")
public class PoiFavorite {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public long id;

    public int type;
    public String posX;
    public String posY;
    public String displayPosX;
    public String displayPosY;
    public long poiId;
    public int childPoiNum;
    public String name;
    public String address;
    public String phoneNumber;
    public String regionName;
    public String typeName;
    public int tagIconId;
    public String tagName;

    String shortcutsType;

    @Override
    public String toString() {
        return " id="+id+" type="+type+" posX="+posX+" posY="+posY+" displayPosX="+displayPosX+" displayPosY="+displayPosY+" poiId="+poiId+
                " childPoiNum="+childPoiNum+" name="+name+" address="+address+" phoneNumber="+phoneNumber+" regionName="+regionName+
                " typeName="+typeName+" tagIconId="+tagIconId+" tagName="+tagName+" shortcutsType="+shortcutsType+" ; ";
    }

    public PoiFavorite(){
    }

    public PoiFavorite(int type,String posX,String posY,String displayPosX,String displayPosY,long poiId,int childPoiNum,String name,String address,String phoneNumber,String regionName,String typeName,int tagIconId,String tagName){
        this.type=type;
        this.posX=posX;
        this.posY=posY;
        this.displayPosX=displayPosX;
        this.displayPosY=displayPosY;
        this.poiId=poiId;
        this.childPoiNum=childPoiNum;
        this.name=name;
        this.address=address;
        this.phoneNumber=phoneNumber;
        this.regionName=regionName;
        this.typeName=typeName;
        this.tagIconId=tagIconId;
        this.tagName=tagName;
    }

    public String getShortcutsType() {
        return shortcutsType;
    }

    public void setShortcutsType(String shortcutsType) {
        this.shortcutsType = shortcutsType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPosX() {
        return posX;
    }

    public void setPosX(String posX) {
        this.posX = posX;
    }

    public String getPosY() {
        return posY;
    }

    public void setPosY(String posY) {
        this.posY = posY;
    }

    public String getDisplayPosX() {
        return displayPosX;
    }

    public void setDisplayPosX(String displayPosX) {
        this.displayPosX = displayPosX;
    }

    public String getDisplayPosY() {
        return displayPosY;
    }

    public void setDisplayPosY(String displayPosY) {
        this.displayPosY = displayPosY;
    }

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
    }

    public int getChildPoiNum() {
        return childPoiNum;
    }

    public void setChildPoiNum(int childPoiNum) {
        this.childPoiNum = childPoiNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTagIconId() {
        return tagIconId;
    }

    public void setTagIconId(int tagIconId) {
        this.tagIconId = tagIconId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

}
