package com.cxytiandi.encrypt.dto;

public class RsaKeyInfoDto {
	private String modulus;
	private String publicExponent;
	private String privateExponent;

	public RsaKeyInfoDto(String modulus, String publicExponent, String privateExponent) {
		super();
		this.modulus = modulus;
		this.publicExponent = publicExponent;
		this.privateExponent = privateExponent;
	}

	public String getModulus() {
		return modulus;
	}

	public void setModulus(String modulus) {
		this.modulus = modulus;
	}

	public String getPublicExponent() {
		return publicExponent;
	}

	public void setPublicExponent(String publicExponent) {
		this.publicExponent = publicExponent;
	}

	public String getPrivateExponent() {
		return privateExponent;
	}

	public void setPrivateExponent(String privateExponent) {
		this.privateExponent = privateExponent;
	}

}
