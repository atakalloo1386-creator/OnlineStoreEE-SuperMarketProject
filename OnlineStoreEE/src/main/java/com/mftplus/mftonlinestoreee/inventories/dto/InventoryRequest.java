package com.mftplus.mftonlinestoreee.inventories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InventoryRequest(
        @NotBlank @Size(max = 60) String name,
        @NotBlank @Size(max = 60) String location){
}