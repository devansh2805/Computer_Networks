import java.util.Scanner;

class InternetCheckSum {
	public static void main(String[] args) {
		Scanner scannerObject = new Scanner(System.in);
		System.out.print("Enter Text to Transmit: ");
		String text = scannerObject.nextLine();
		System.out.println("-----------------------------------------SENDER SIDE-----------------------------------------");
		char[] textArray = text.toCharArray();
		StringBuilder hexText = new StringBuilder();
		for(char textElement: textArray) {
			int charAsciiValue = (int)textElement;
			hexText.append(Integer.toHexString(charAsciiValue));
			System.out.print(textElement + "->" + Integer.toHexString(charAsciiValue) + " ");
		}
		System.out.println();
		while(hexText.length()%16 != 0) {
			hexText.append(0);
		}
		String[] hexPartText = new String[hexText.length()/4];
		for(int i=0;i<hexText.length();i+=4) {
			hexPartText[(int)i/4] = hexText.toString().substring(i, i+4); 
		}
		String unWrappedCheckSum = Hexadecimal.checkSumAddition(hexPartText);
		String wrappedCheckSum = Hexadecimal.wrapCheckSum(unWrappedCheckSum);
		String checkSum = Hexadecimal.finalCheckSum(wrappedCheckSum);
		int count = 0;
		for(String hex: hexPartText) {
			char[] hexArray = hex.toCharArray();
			if(count == 0) {
				System.out.print("Check Sum Calculation.....:    ");
				count++;
			} else {
				System.out.print("                               ");
			}
			for(char hexElement: hexArray) {
				System.out.print(padRight(Character.toString(hexElement), 2));
			}
			System.out.println();
		}
		System.out.print("Initialized Check Sum.....:    ");
		System.out.print("0 0 0 0\n");
		System.out.println("                             -----------");
		System.out.print("Un-Wrapped Check Sum......:  ");
		for(char unWrapElement: unWrappedCheckSum.toCharArray()) {
			System.out.print(padRight(Character.toString(unWrapElement), 2));
		}
		System.out.println();
		System.out.print("Wrapped Check Sum.........:    ");
		for(char wrapElement: wrappedCheckSum.toCharArray()) {
			System.out.print(padRight(Character.toString(wrapElement), 2));
		}
		System.out.println();
		System.out.print("Check Sum.................:    ");
		for(char checkSumElement: checkSum.toCharArray()) {
			System.out.print(padRight(Character.toString(checkSumElement), 2));
		}
		System.out.println("\n");
		System.out.print("Enter Text Recieved (Enter Hexadecimal String Recieved): ");
		StringBuilder recHexText = new StringBuilder(scannerObject.nextLine().toLowerCase());
		System.out.println("----------------------------------------RECEIVER SIDE----------------------------------------");
		// char[] recTextArray = text.toCharArray();
		// StringBuilder recHexText = new StringBuilder();
		// for(char textElement: recTextArray) {
		// 	int charAsciiValue = (int)textElement;
		// 	recHexText.append(Integer.toHexString(charAsciiValue));
		// }
		String[] recHexPartText = new String[(recHexText.length()/4)];
		for(int i=0;i<recHexText.length();i+=4) {
			recHexPartText[(int)i/4] = recHexText.toString().substring(i, i+4); 
		}
		String recUnWrappedCheckSum = Hexadecimal.checkSumAddition(recHexPartText);
		String recWrappedCheckSum = Hexadecimal.wrapCheckSum(recUnWrappedCheckSum);
		String recCheckSum = Hexadecimal.finalCheckSum(recWrappedCheckSum);
		count = 0;
		for(String hex: recHexPartText) {
			char[] hexArray = hex.toCharArray();
			if(count == 0) {
				System.out.print("Check Sum Calculation.....:    ");
			} else if(count == recHexPartText.length-1) {
				System.out.print("Check Sum Recieved........:    ");
			} else {
				System.out.print("                               ");
			}
			for(char hexElement: hexArray) {
				System.out.print(padRight(Character.toString(hexElement), 2));
			}
			count++;
			System.out.println();
		}
		System.out.println("                             -----------");
		System.out.print("Un-Wrapped Check Sum......:  ");
		for(char recUnWrapElement: recUnWrappedCheckSum.toCharArray()) {
			System.out.print(padRight(Character.toString(recUnWrapElement), 2));
		}
		System.out.println();
		System.out.print("Wrapped Check Sum.........:    ");
		for(char recWrapElement: recWrappedCheckSum.toCharArray()) {
			System.out.print(padRight(Character.toString(recWrapElement), 2));
		}
		System.out.println();
		System.out.print("Check Sum.................:    ");
		for(char recCheckSumElement: recCheckSum.toCharArray()) {
			System.out.print(padRight(Character.toString(recCheckSumElement), 2));
		}
		if(recCheckSum.equals("0000")) {
			System.out.println("\n\nConclusion: Transmission Resulted in No Error");
		} else {
			System.err.println("\n\nConclusion: Transmission Resulted into Error");
		}
		scannerObject.close();
	}

	public static String padRight(String s, int n) {
    	return String.format("%-"+n+"s",s);  
	}
}

class Hexadecimal {
	public static String checkSumAddition(String[] hexValues) {
		int sum = 0, hexSum = 0, hexCarry = 0;
		StringBuilder hexadecimalSumString = new StringBuilder();
		for(int i=3;i>=0;i--) {
			sum = hexCarry;
			for(String hex: hexValues) {
				sum += Integer.parseInt(Character.toString(hex.charAt(i)), 16);
			}
			hexSum = sum % 16;
			hexCarry = sum / 16;
			if(i != 0 || hexCarry == 0) {
				hexadecimalSumString.insert(0, Integer.toHexString(hexSum));
			} else {
				hexadecimalSumString.insert(0, Integer.toHexString(hexSum));
				hexadecimalSumString.insert(0, Integer.toHexString(hexCarry)); 
			}
		}
		return hexadecimalSumString.toString();
	}

	public static String wrapCheckSum(String unWrappedCheckSum) {
		StringBuilder hexadecimalSumString = new StringBuilder(unWrappedCheckSum);
		while(hexadecimalSumString.length()!=4) {
			char extraChar = hexadecimalSumString.charAt(0);
			hexadecimalSumString = hexadecimalSumString.deleteCharAt(0);
			hexadecimalSumString = simpleHexAddition(hexadecimalSumString, extraChar);
		}
		return hexadecimalSumString.toString();
	}

	public static StringBuilder simpleHexAddition(StringBuilder sumString, char addValue) {
		int sum = 0, hexSum = 0, hexCarry = 0;
		StringBuilder wrapSum = new StringBuilder();
		for(int i=3;i>=0;i--) {
			sum = hexCarry;
			if(i==3) {
				sum += Integer.parseInt(Character.toString(sumString.charAt(i)), 16) + Integer.parseInt(Character.toString(addValue), 16);
			} else {
				sum += Integer.parseInt(Character.toString(sumString.charAt(i)), 16);
			}
			hexSum = sum % 16;
			hexCarry = sum / 16;
			if(i != 0 || hexCarry == 0) {
				wrapSum.insert(0, Integer.toHexString(hexSum));
			} else {
				wrapSum.insert(0, Integer.toHexString(hexSum));
				wrapSum.insert(0, Integer.toHexString(hexCarry)); 
			}
		}
		return wrapSum;
	}

	public static String finalCheckSum(String wrappedSum) {
		StringBuilder checkSum = new StringBuilder(wrappedSum);
		String finalSum = "";
		for(int i=0;i<checkSum.length();i++) {
			finalSum += Integer.toHexString(15 - Integer.parseInt(Character.toString(checkSum.charAt(i)), 16));
		}
		return finalSum;
	}
}