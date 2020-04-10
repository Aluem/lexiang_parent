package top.lexiang.encrypt.rsa;

/**
 * rsa加解密用的公钥和私钥
 * @author Administrator
 *
 */
public class RsaKeys {

	//生成秘钥对的方法可以参考这篇帖子
	//https://www.cnblogs.com/yucy/p/8962823.html

//	//服务器公钥
//	private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6Dw9nwjBmDD/Ca1QnRGy"
//											 + "GjtLbF4CX2EGGS7iqwPToV2UUtTDDemq69P8E+WJ4n5W7Iln3pgK+32y19B4oT5q"
//											 + "iUwXbbEaAXPPZFmT5svPH6XxiQgsiaeZtwQjY61qDga6UH2mYGp0GbrP3i9TjPNt"
//											 + "IeSwUSaH2YZfwNgFWqj+y/0jjl8DUsN2tIFVSNpNTZNQ/VX4Dln0Z5DBPK1mSskd"
//											 + "N6uPUj9Ga/IKnwUIv+wL1VWjLNlUjcEHsUE+YE2FN03VnWDJ/VHs7UdHha4d/nUH"
//											 + "rZrJsKkauqnwJsYbijQU+a0HubwXB7BYMlKovikwNpdMS3+lBzjS5KIu6mRv1GoE"
//											 + "vQIDAQAB";
//
//	//服务器私钥(经过pkcs8格式处理)
//	private static final String serverPrvKeyPkcs8 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDoPD2fCMGYMP8J"
//				 								 + "rVCdEbIaO0tsXgJfYQYZLuKrA9OhXZRS1MMN6arr0/wT5YniflbsiWfemAr7fbLX"
//				 								 + "0HihPmqJTBdtsRoBc89kWZPmy88fpfGJCCyJp5m3BCNjrWoOBrpQfaZganQZus/e"
//				 								 + "L1OM820h5LBRJofZhl/A2AVaqP7L/SOOXwNSw3a0gVVI2k1Nk1D9VfgOWfRnkME8"
//				 								 + "rWZKyR03q49SP0Zr8gqfBQi/7AvVVaMs2VSNwQexQT5gTYU3TdWdYMn9UeztR0eF"
//				 								 + "rh3+dQetmsmwqRq6qfAmxhuKNBT5rQe5vBcHsFgyUqi+KTA2l0xLf6UHONLkoi7q"
//				 								 + "ZG/UagS9AgMBAAECggEBANP72QvIBF8Vqld8+q7FLlu/cDN1BJlniReHsqQEFDOh"
//				 								 + "pfiN+ZZDix9FGz5WMiyqwlGbg1KuWqgBrzRMOTCGNt0oteIM3P4iZlblZZoww9nR"
//				 								 + "sc4xxeXJNQjYIC2mZ75x6bP7Xdl4ko3B9miLrqpksWNUypTopOysOc9f4FNHG326"
//				 								 + "0EMazVaXRCAIapTlcUpcwuRB1HT4N6iKL5Mzk3bzafLxfxbGCgTYiRQNeRyhXOnD"
//				 								 + "eJox64b5QkFjKn2G66B5RFZIQ+V+rOGsQElAMbW95jl0VoxUs6p5aNEe6jTgRzAT"
//				 								 + "kqM2v8As0GWi6yogQlsnR0WBn1ztggXTghQs2iDZ0YkCgYEA/LzC5Q8T15K2bM/N"
//				 								 + "K3ghIDBclB++Lw/xK1eONTXN+pBBqVQETtF3wxy6PiLV6PpJT/JIP27Q9VbtM9UF"
//				 								 + "3lepW6Z03VLqEVZo0fdVVyp8oHqv3I8Vo4JFPBDVxFiezygca/drtGMoce0wLWqu"
//				 								 + "bXlUmQlj+PTbXJMz4VTXuPl1cesCgYEA6zu5k1DsfPolcr3y7K9XpzkwBrT/L7LE"
//				 								 + "EiUGYIvgAkiIta2NDO/BIPdsq6OfkMdycAwkWFiGrJ7/VgU+hffIZwjZesr4HQuC"
//				 								 + "0APsqtUrk2yx+f33ZbrS39gcm/STDkVepeo1dsk2DMp7iCaxttYtMuqz3BNEwfRS"
//				 								 + "kIyKujP5kfcCgYEA1N2vUPm3/pNFLrR+26PcUp4o+2EY785/k7+0uMBOckFZ7GIl"
//				 								 + "FrV6J01k17zDaeyUHs+zZinRuTGzqzo6LSCsNdMnDtos5tleg6nLqRTRzuBGin/A"
//				 								 + "++xWn9aWFT+G0ne4KH9FqbLyd7IMJ9R4gR/1zseH+kFRGNGqmpi48MS61G0CgYBc"
//				 								 + "PBniwotH4cmHOSWkWohTAGBtcNDSghTRTIU4m//kxU4ddoRk+ylN5NZOYqTxXtLn"
//				 								 + "Tkt9/JAp5VoW/41peCOzCsxDkoxAzz+mkrNctKMWdjs+268Cy4Nd09475GU45khb"
//				 								 + "Y/88qV6xGz/evdVW7JniahbGByQhrMwm84R9yF1mNwKBgCIJZOFp9xV2997IY83S"
//				 								 + "habB/YSFbfZyojV+VFBRl4uc6OCpXdtSYzmsaRcMjN6Ikn7Mb9zgRHR8mPmtbVfj"
//				 								 + "B8W6V1H2KOPfn/LAM7Z0qw0MW4jimBhfhn4HY30AQ6GeImb2OqOuh3RBbeuuD+7m"
//				 								 + "LpFZC9zGggf9RK3PfqKeq30q";

	//服务器公钥
	private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwGZYqU0ESJJtjymPwC8L" +
			"kd+elHswrOL8QNJmWnW4A61ZeSX60k7bimYtP3LriSCo0DoSbxSUPTeNzq80400I" +
			"SGQsdWpjib9PHCjmSxZ6L9ruqRE/POoPlxBxqxuZBhOYLKw2OPv5xw9hcgKWM+/q" +
			"LYqL5nlfo6zESRAkxcCohuJa/I/qk/vyno8AabD+SB0jArPlPrI88EpbQ3J1rXCS" +
			"XeVlu6YaQvQu1HL/CIF0OyWJ+QHZ4uGQ5C2Y835lEfS6Hdo80ITx+p3MuYdLzAP3" +
			"DRgLN3DNao9AScqRB2/qO9SbMNa5iZA9Fa6fuzg2EYbOyjbvbMStU8RHu8er3Fn0" +
			"1QIDAQAB";

	//服务器私钥(经过pkcs8格式处理)
	private static final String serverPrvKeyPkcs8 = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDAZlipTQRIkm2P" +
			"KY/ALwuR356UezCs4vxA0mZadbgDrVl5JfrSTtuKZi0/cuuJIKjQOhJvFJQ9N43O" +
			"rzTjTQhIZCx1amOJv08cKOZLFnov2u6pET886g+XEHGrG5kGE5gsrDY4+/nHD2Fy" +
			"ApYz7+otiovmeV+jrMRJECTFwKiG4lr8j+qT+/KejwBpsP5IHSMCs+U+sjzwSltD" +
			"cnWtcJJd5WW7phpC9C7Ucv8IgXQ7JYn5Adni4ZDkLZjzfmUR9Lod2jzQhPH6ncy5" +
			"h0vMA/cNGAs3cM1qj0BJypEHb+o71Jsw1rmJkD0Vrp+7ODYRhs7KNu9sxK1TxEe7" +
			"x6vcWfTVAgMBAAECggEBAJ0adUc5Nkg0GPYPj+vz/lSM7qZ7uRFS/3vG0gEHosyN" +
			"3kOIebkE30uxhz0KD74XRwFQigDeIvWhyU99zYfVPPAvrAXsa37tEiICW5nwrrEo" +
			"6jxvct3Amh5WNRrtHBTUJyxDKrt5NUoQCxfXeOLMAsJRIU9+yFOUHWsznXNEj9gi" +
			"zojIdw7z2lgcMULxbMLlOo+NgcnnGIrlR0k4FJS6N2iBEjsh8flZ1B37SA14feyz" +
			"aQUMPTvGVXDeNwbh4oIVQ03Q026czYH4JX1FRCNPLg8ug8ycIBwDzVKJ6hDfTwtG" +
			"3E/dMxbPBu3jN7N9oVgZDQNQDUI96GMyP7HZDtyvgHkCgYEA36VAYkbnZhUS8NrD" +
			"Nf5zDagAD3a/5Nz3mRe45tVmDnudEpPFanTc/pV/YLgoAk7O24htVEoRepwD/Eq2" +
			"/XOdaa5gZyJ5ArlNlDRlFn4JJ8mB58CRlLbf8EOhcultcErF0c/SFrJ+v9RtfTUn" +
			"Stn/IK2drnS1oMSkCJRCcGKcviMCgYEA3DvngTlPLEcXsLCy+zvPgT6JAQ7d8Z2/" +
			"fkbcjqOdzry/H0AozUu0VD/pSQZXa2laFrve/qB6gjotJwe0RcJME0FPyeoXePei" +
			"j+Ju4r49USRugUADAqnZiZwZFFhO2GFpEYFtBgSIb09/95mgHOmK7YOvMIVY7U5Z" +
			"r62nic5/JKcCgYEA3KY6nnucA8BY/p6nKJdRxjBxVtBvRQqpkjawa7YxSaq17sUy" +
			"afTAsMCoerFWb+h2+Gtiil3FXvWcjQbeAbNWpBSx8i81uvU90DHdZlKVD9ckvpTk" +
			"TRgcWv0uxaP+IGL1RyrF+TTEDtkz6OtPR+9KYmBIiP2G0QYRXJGB90WjpYcCgYEA" +
			"gaLdbep/GTAr2LzaThpx1cEqwi2vJVdW7gzmLSxQau4djlak7dtfJayNNIgAE2Dr" +
			"P7CM3dM26cs24t+dFOb/AuKSBee+s10JGqsN3Hhg/Y91YK1+WCYEwATbmmc02hfU" +
			"0hIeS8mFSghA6k1Ku1oZhh/akrQbmWxiTI/qq6qamscCgYEAqkGthHBY7N1nh8/D" +
			"juILqysSUYx65dRMFB4VSx9p4VUbMnb8Y/laATZp635V7iS00oUBMmlgtUw/svNc" +
			"mNE7NA/NbNP4nTfR9RsV6nmzhRh82E8KCBOjB4JAD+wSgaeWdZOpmpISBKO/RTgT" +
			"6vEuVZEG4uY0R3rlY/wGifQTNIM=";

	public static String getServerPubKey() {
		return serverPubKey;
	}

	public static String getServerPrvKeyPkcs8() {
		return serverPrvKeyPkcs8;
	}
	
}
