package com.tfandkusu.siwatest

/**
 * ユーザ情報
 * @param id ユーザのID
 * @param email メールアドレス
 * @param verify 検証済みフラグ
 */
data class User(val id: String, val email: String, val verify: Boolean)