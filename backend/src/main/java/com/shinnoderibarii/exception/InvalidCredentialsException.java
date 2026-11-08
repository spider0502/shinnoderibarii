
package com.shinnoderibarii.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("メールアドレスまたはパスワードが正しくありません");
    }
}
