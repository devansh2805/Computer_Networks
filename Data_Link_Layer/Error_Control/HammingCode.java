import java.util.Scanner;

class HammingCode {
	// Main Method
	public static void main(String[] args) {
		System.out.println("----------------------------------------------Hamming Code Implementation---------------------------------------------");
		String dataWord = "", receivedCodeWord = "";
		Scanner scannerObject = new Scanner(System.in);
		System.out.print("Enter Data Word: ");
		dataWord = scannerObject.next();
		System.out.print("Enter Received Code Word: ");
		receivedCodeWord = scannerObject.next();
		System.out.println("\n------------------------------TRANSMITTER SIDE RESULTS------------------------------");
		HammingCodeEncoder encoder = new HammingCodeEncoder(dataWord);
		encoder.encodeDataWord(); // Encoder call
		System.out.println("Data Word....................: " + encoder.dataWord.toString());
		System.out.println("Transmitted Code Word........: " + encoder.codeWord.toString());
		System.out.println("\n--------------------------------RECEIVER SIDE RESULTS-------------------------------");
		HammingCodeDecoder decoder = new HammingCodeDecoder(receivedCodeWord, encoder.numberOfParityBits);
		System.out.println("Received Code Word...........: " + decoder.receivedCodeWord.toString());
		decoder.decodeCodeWord(); // Decoder call
		// Concluding Hamming Code Implementation
		System.out.print("\nConclusion: ");
		if(decoder.decodedDataWord.toString().equals(dataWord)) {
			if(!decoder.errorDetection()) {
				System.out.println("Transmission Resulted into no Error, Hamming Code detected no Error.");
			} else {
				System.out.println("Transmission Resulted into single bit Error, Hamming Code detected the Error and Corrected the same.");
			}
		} else {
			System.err.println("Transmission Resulted into multi bit Error, Hamming Code detected Error but could not Correct the Error.");
		}
		scannerObject.close();
	}

	// Converts a Binary String into a specific Length. (By Appending 0s)
	public static StringBuilder toProperBinary(String binaryString, int codeWordLength) {
		int bitsRequired = Integer.toBinaryString(codeWordLength+1).length();
		if(binaryString.length() == bitsRequired) {
			return new StringBuilder(binaryString);
		} else if(binaryString.length() < bitsRequired) {
			String temp = "";
			for(int i=0;i<bitsRequired-binaryString.length();i++) {
				temp += "0";
			}
			return new StringBuilder(temp + binaryString);
		} else {
			return new StringBuilder(binaryString);
		}
	}
}

// Encoder Class
class HammingCodeEncoder {
	StringBuilder dataWord, codeWord;
	final int dataWordLength, codeWordLength, numberOfParityBits;

	// Constructor for Encoder Class
	HammingCodeEncoder(String dataWord) {
		this.dataWord = new StringBuilder(dataWord);
		dataWordLength = dataWord.length();
		numberOfParityBits = calculateNumberOfParityBits();
		codeWordLength = dataWordLength + numberOfParityBits;
		codeWord = new StringBuilder(this.dataWord.toString());
	}

	// Calculates number of Parity Bits. Condition is 2^m - m - 1 >= k
	// m = number of parity bits, k = data word length
	public int calculateNumberOfParityBits() {
		int parityBits = 0;
		while((int)Math.pow(2, parityBits) - 1 - parityBits < dataWordLength) {
			parityBits++;
		}
		return parityBits;
	}

	// Encodes The Data word with additional Parity bits.
	public void encodeDataWord() {
		int indexPosition = 0, index = 0, xorValue = 0;
		StringBuilder printingCodeWord = new StringBuilder(dataWord.toString());
		for(int i=0;i<numberOfParityBits;i++) {
			index = (int)Math.pow(2, i) - 1;
			codeWord.insert(codeWord.length()-index, 0);
			printingCodeWord.insert(printingCodeWord.length()-index,'P');
		}
		System.out.println("Parity Bits Placement........: " + printingCodeWord.toString());
		System.out.println("Parity Bits Calculation......: ParityBit = xor logic");
		for(int i=0;i<numberOfParityBits; i++) {
			xorValue = 0;
			index = (int)Math.pow(2, i);
			indexPosition = HammingCode.toProperBinary(Integer.toBinaryString(index), codeWordLength).toString().indexOf("1");
			System.out.print("                               P" + index + " = ");
			String xorEquation = "";
			for(int j=1;j<=codeWord.length();j++) {
				if(HammingCode.toProperBinary(Integer.toBinaryString(j), codeWordLength).charAt(indexPosition) == '1') {
					int bitValue = Character.getNumericValue(codeWord.charAt(codeWord.length()-j));
					xorValue ^= bitValue; 
					xorEquation += Integer.toString(bitValue) + " xor ";
				}
			}
			xorEquation = xorEquation.substring(5, xorEquation.length()-5);
			System.out.println(xorEquation + " = " + xorValue);
			codeWord = codeWord.replace(codeWord.length()-index, codeWord.length()-index+1, Integer.toString(xorValue));
		}
	}
}

// Decoder Class
class HammingCodeDecoder {
	StringBuilder receivedCodeWord, syndromeBits, decodedDataWord;
	final int codeWordLength, numberOfSyndromeBits;

	// Constructor for Decoder Class
	HammingCodeDecoder(String receivedCodeWord, int numberOfSyndromeBits) {
		this.receivedCodeWord = new StringBuilder(receivedCodeWord);
		this.numberOfSyndromeBits = numberOfSyndromeBits;
		codeWordLength = this.receivedCodeWord.length() - this.numberOfSyndromeBits;
		syndromeBits = new StringBuilder();
		decodedDataWord = new StringBuilder();
	}

	// Decodes Code Word as well as checks for error.
	// In case of single bit error it will successfully correct the error.
	public void decodeCodeWord() {
		int index = 0, indexPosition = 0, xorValue = 0;
		System.out.println("Syndrome Bits Calculation....: SyndromeBit = xor logic");
		for(int i=0;i<numberOfSyndromeBits;i++) {
			xorValue = 0;
			index = (int)Math.pow(2, i);
			indexPosition = HammingCode.toProperBinary(Integer.toBinaryString(index), codeWordLength).toString().indexOf("1");
			System.out.print("                               S" + index + " = ");
			String xorEquation = "";
			for(int j=1;j<=receivedCodeWord.length();j++) {
				if(HammingCode.toProperBinary(Integer.toBinaryString(j), codeWordLength).charAt(indexPosition) == '1') {
					int bitValue = Character.getNumericValue(receivedCodeWord.charAt(receivedCodeWord.length()-j));
					xorValue ^= bitValue;
					xorEquation += Integer.toString(bitValue) + " xor ";
				}
			}
			xorEquation = xorEquation.substring(0, xorEquation.length()-5);
			System.out.println(xorEquation + " = " + xorValue);
			syndromeBits.insert(i, xorValue);
		}
		System.out.println("Syndrome Bits are............: " + getSyndromeSequence());
		if(this.errorDetection()) {
			System.out.println("Error Detection Status.......: Error Detected");
			System.out.println("Bit Position In Error........: " + errorCorrection() + " (Position from LSB)");
			System.out.println("Corrected Code Word..........: " + receivedCodeWord.toString());
		} else {
			System.out.println("Error Detection Status.......: No Error Detected");
		}
		for(int i=receivedCodeWord.length()-1;i>=0;i--) {
			String binary = Integer.toBinaryString(receivedCodeWord.length()-i);
			if(binary.substring(1, binary.length()).indexOf("1") == -1) {
				continue;
			} else {
				decodedDataWord.append(receivedCodeWord.charAt(i));
			}
		}
		System.out.println("Decoded Code Word............: " + decodedDataWord.reverse().toString());
	}

	// Returns Syndrome Bits in specified Order
	public String getSyndromeSequence() {
		syndromeBits = syndromeBits.reverse();
		String syndromeSequence = "{ ";
		for(int i=syndromeBits.length()-1;i>=0;i--) {
			 syndromeSequence += "S" + (int)Math.pow(2,i)+", ";
		}
		syndromeSequence = syndromeSequence.substring(0, syndromeSequence.length()-2);
		syndromeSequence += " } = { ";
		for (char syndromeBit : syndromeBits.toString().toCharArray()) {
			syndromeSequence += syndromeBit + ", ";
		}
		syndromeSequence = syndromeSequence.substring(0, syndromeSequence.length()-2);
		syndromeSequence += " }";
		return syndromeSequence;
	}

	// Error Detection Logic based on Syndrome Bits
	public boolean errorDetection() {
		if(syndromeBits.toString().indexOf("1") == -1) {
			return false; // No Error
		} else {
			return true; // Yes Error
		}
	}

	// Error Correction Logic based on Decimal Value of Syndrome Bits
	public int errorCorrection() {
		int errorPosition = Integer.parseInt(syndromeBits.toString(),2);
		int indexOfError = receivedCodeWord.length()-errorPosition;
		int bit = (Character.getNumericValue(receivedCodeWord.charAt(indexOfError)));
		if(bit == 0) {
			bit = 1;
		} else {
			bit = 0;
		}
		receivedCodeWord = receivedCodeWord.replace(indexOfError, indexOfError+1, Integer.toString(bit));
		return errorPosition;
	}
}