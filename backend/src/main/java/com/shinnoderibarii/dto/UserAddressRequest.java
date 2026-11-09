package com.shinnoderibarii.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddressRequest {

    @NotBlank(message = "收件人姓名不能为空")
    @Size(max = 100, message = "收件人姓名不能超过100个字符")
    private String recipientName;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    @NotBlank(message = "邮政编码不能为空")
    @Size(max = 10, message = "邮政编码不能超过10个字符")
    private String postalCode;

    @NotBlank(message = "配送地址不能为空")
    @Size(max = 255, message = "配送地址不能超过255个字符")
    private String address;
}
