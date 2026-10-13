
package com.shinnoderibarii.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = "名前は必須です") @Size(max = 100, message = "名前は100文字以内で入力してください") String name,

        @NotBlank(message = "メールアドレスは必須です") @Email(message = "メールアドレスの形式が正しくありません") @Size(max = 255, message = "メールアドレスは255文字以内で入力してください") String email,

        @NotBlank(message = "パスワードは必須です") @Size(min = 8, max = 72, message = "パスワードは8文字以上72文字以内で入力してください") String password) {
}
