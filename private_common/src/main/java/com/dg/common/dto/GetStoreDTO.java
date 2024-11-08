package com.dg.common.dto;

import lombok.Data;

@Data
public class GetStoreDTO {

    byte[] data;

    private StoreInfoDTO storeInfoDTO;
}
