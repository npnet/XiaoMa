package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.BasePageResult;
import com.kaolafm.opensdk.api.media.model.AudioDetails;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class XMListPageAudioDetails extends XMPageResult<XMAudioDetails, AudioDetails> {

    public XMListPageAudioDetails(BasePageResult<List<AudioDetails>> listBasePageResult) {
        super(listBasePageResult);
    }

    @Override
    protected XMAudioDetails handleConverter(AudioDetails audioDetails) {
        return new XMAudioDetails(audioDetails);
    }

    @Override
    public String toString() {
        return getSDKBean().toString();
    }
}
