package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SpuAttrValueVo extends SpuAttrValueEntity {

    private List<String> valueSelected;

    public void setValueSelected(List<String> valueSelected) {
        if (valueSelected != null) {
            this.setAttrValue(StringUtils.join(valueSelected));
        }
    }
}
