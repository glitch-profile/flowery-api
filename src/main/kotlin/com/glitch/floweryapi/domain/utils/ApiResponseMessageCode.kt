package com.glitch.floweryapi.domain.utils

enum class ApiResponseMessageCode {
    //success
    OK,
    //general errors
    USER_NOT_FOUND,
    //sessions errors
    SESSION_NOT_FOUND,
    //login errors
    AUTH_DATA_INCORRECT,
    PHONE_NOT_FOUND,
    PHONE_ALREADY_IN_USE,
    PHONE_INCORRECT,
    CODE_INCORRECT,


}