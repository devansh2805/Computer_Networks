import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.Collections;

class Networking {
	public static void main(String[] args) {
		Scanner scannerObject = new Scanner(System.in);
		System.out.print("\nEnter IP Address: ");
		String ip = scannerObject.next();
		if(!Network.validateIP(ip)) {
			scannerObject.close();
			System.err.println("Error!! Invalid IP Address");
			return;
		}
		System.out.print("Enter Mask: /");
		int mask = scannerObject.nextInt();
		if(mask > 32) {
			scannerObject.close();
			System.err.println("Error!! Mask cannot be greater than 32");
			return;
		}
		Network networkObject = new Network(ip, mask);
		
		int numberOfIPs = (int)Math.pow(2, 32-mask);
		System.out.print("Enter number of Subnets to make: ");
		int subnetValue = scannerObject.nextInt();
		System.out.print("Are each subnet equal in size? (y/n): ");
		char subnetChar = scannerObject.next().charAt(0);
		boolean subnetFlag = true;
		Integer[] sizeOfBlocks = new Integer[subnetValue];
		int sum = 0;
		if(subnetChar == 'y') {
			sizeOfBlocks = null;
			subnetFlag = true;
		} else if(subnetChar == 'n') {
			System.out.print("Enter size of each subnet block: ");
			for (int i=0;i<subnetValue;i++) {
				sizeOfBlocks[i] = Integer.valueOf(scannerObject.nextInt());
				sum += sizeOfBlocks[i].intValue();
			}
			Arrays.sort(sizeOfBlocks, Collections.reverseOrder());
			subnetFlag = false;
		} else {
			System.err.println("Error!! Wrong Input");
			return;
		}

		System.out.println("\n------------------------------RESULT------------------------------\n");
		networkObject.getNetworkClass();
		networkObject.printFullNetworkDetails();
		System.out.println();
		if(subnetFlag) {
			if(numberOfIPs%subnetValue == 0) {
				int n = 0;
				while(true) {
					if((int)Math.pow(2, n) == numberOfIPs/subnetValue) {
						for(int i=0; i<subnetValue; i++) {
							generateNewSubnet(numberOfIPs/subnetValue, 32-n, (char)(65+i));
						}
						break;
					} else if(n > 32 - mask) {
						break;
					}
					n++;
				}
			} else {
				int n = 0;
				while(true) {
					if(((int)Math.pow(2, n) < numberOfIPs/subnetValue) && ((int)Math.pow(2, n+1) > numberOfIPs/subnetValue)) {
						for(int i=0; i<subnetValue; i++) {
							generateNewSubnet((int)Math.pow(2, n), 32-n, (char)(65+i));
						}
						System.out.println("-----------------------SUBNETTING INFO--------------------------");
						System.out.println("Range of IPs Alloted........: " + networkObject.firstIP + " - " + Network.ipCounter);
						System.out.println("Range of IPs Available......: " + Network.generateNewIP(Network.ipCounter) + " - " + networkObject.lastIP);
						break;
					} else {
						n++;
					}
				}
			}
		} else {
			if(sum == numberOfIPs) {
				int index = 0;
				for(Integer blockSize : sizeOfBlocks) {
					int n = 0;
					while(true) {
						if((int)Math.pow(2, n) == blockSize.intValue()) {
							generateNewSubnet((int)Math.pow(2, n), 32-n, (char)(65+index++));
							break;
						} else if(((int)Math.pow(2, n) < blockSize.intValue()) && ((int)Math.pow(2, n+1) > blockSize.intValue())) {
							generateNewSubnet(blockSize.intValue(), 32-n-1, (65+index++));
							break;
						}
						n++;
					}
				}
				if(!Network.ipCounter.equals(networkObject.lastIP)) {
					System.out.println("-----------------------SUBNETTING INFO--------------------------");
						System.out.println("Range of IPs Alloted........: " + networkObject.firstIP + " - " + Network.ipCounter);
						System.out.println("Range of IPs Available......: " + Network.generateNewIP(Network.ipCounter) + " - " + networkObject.lastIP);
				}
			} else if(sum < numberOfIPs) {
				int index = 0;
				for(Integer blockSize : sizeOfBlocks) {
					int n = 0;
					while(true) {
						if((int)Math.pow(2, n) == blockSize.intValue()) {
							generateNewSubnet((int)Math.pow(2, n), 32-n, (char)(65+index++));
							break;
						} else if(((int)Math.pow(2, n) < blockSize.intValue()) && ((int)Math.pow(2, n+1) > blockSize.intValue())) {
							generateNewSubnet(blockSize.intValue(), 32-n-1, (65+index++));
							break;
						}
						n++;
					}
				}
				if(!Network.ipCounter.equals(networkObject.lastIP)) {
					System.out.println("-----------------------SUBNETTING INFO--------------------------");
						System.out.println("Range of IPs Alloted........: " + networkObject.firstIP + " - " + Network.ipCounter);
						System.out.println("Range of IPs Available......: " + Network.generateNewIP(Network.ipCounter) + " - " + networkObject.lastIP);
				}
			} else {
				int index = 0, count = 0, n = 0;
				for(Integer blockSize: sizeOfBlocks) {
					n = 0;
					if(count < numberOfIPs) {
						while(true) {
							if((int)Math.pow(2, n) == blockSize.intValue()) {
								generateNewSubnet((int)Math.pow(2, n), 32-n, (char)(65+index++));
								if(index != 1) {
									count += blockSize.intValue();
								}
								break;
							} else if(((int)Math.pow(2, n) < blockSize.intValue()) && ((int)Math.pow(2, n+1) > blockSize.intValue())) {
								generateNewSubnet(blockSize.intValue(), 32-n-1, (65+index++));
								if(index != 1) {
									count += (int)Math.pow(2, n+1);
								}
								break;
							}
							n++;
						}
					} else if(count == numberOfIPs) {
						break;
					} else {
						int lastSize = Integer.parseInt(networkObject.lastIP.split("[.]")[3]) - Integer.parseInt(Network.ipCounter.split("[.]")[3]);
						while(true) {
							if(lastSize == 0) {
								break;
							}
							if((int)Math.pow(2, n) == lastSize) {
								generateNewSubnet((int)Math.pow(2, n), 32-n, (char)(65+index++));
								break;
							}
							n++;
						}
					}
					n = 0;
					while(true) {
						if((int)Math.pow(2, n) == sizeOfBlocks[0].intValue()) {
							count += sizeOfBlocks[0].intValue();
							break;
						} else if(((int)Math.pow(2, n) < sizeOfBlocks[0].intValue()) && ((int)Math.pow(2, n+1) > sizeOfBlocks[0].intValue())) {
							count += (int)Math.pow(2, n+1);
							break;
						}
						n++;
					}
				}
			}
		}
		System.out.println("\n-----------------------------END-------------------------------\n");
		scannerObject.close();
	}

	public static void generateNewSubnet(int numberOfIPs, int mask, char index) {
		String newIP = Network.generateNewIP(Network.ipCounter);
		Network subNetworkObject = new Network(newIP, mask);
		Network.ipCounter = subNetworkObject.lastIP;
		subNetworkObject.printSubnetDetails(numberOfIPs, index);
	} 

	public static void generateNewSubnet(int numberOfIPs, int mask, int index) {
		String newIP = Network.generateNewIP(Network.ipCounter);
		Network subNetworkObject = new Network(newIP, mask);
		Network.ipCounter = subNetworkObject.lastIP;
		subNetworkObject.printSubnetDetails(numberOfIPs, index);
	}
}

class Network {
	public static String ipCounter = "";
	String ip = "", firstIP = "", lastIP = "";
	String[] binaryIP, subnetMask;
	int mask = 0;
	
	Network(String ip, int mask) {
		this.ip = ip;
		this.mask = mask;
		this.binaryIP = this.decimalToBinary(this.ip);
		this.subnetMask = this.getSubnetMask(mask);
		this.firstIP = this.getFirstIP(binaryIP, subnetMask);
		this.lastIP = this.getLastIP(binaryIP, subnetMask);
		ipCounter = firstIP;
	}

	public void printFullNetworkDetails() {
		System.out.println("Subnet Mask ................: " + getDecimalMask());
		System.out.println("First IP Address............: " + firstIP);
		System.out.println("Last IP Address.............: " + lastIP);
		System.out.println("Number of IPs Permissible...: " + (int)Math.pow(2, 32-mask));
	}

	public void printSubnetDetails(int numberOfSubnetIPs, char charIndex) {
		System.out.println("***********************SUBNET " + charIndex + "*************************");
		System.out.println("Block Size..................: " + (int)Math.pow(2, 32-mask));
		System.out.println("Number of IPs Alloted.......: " + numberOfSubnetIPs);
		System.out.println("Subnet Mask.................: " + getDecimalMask());
		System.out.println("First IP Address............: " + firstIP);
		System.out.println("Last IP Address.............: " + lastIP);
		System.out.println();
	}

	public void printSubnetDetails(int numberOfSubnetIPs, int charIndex) {
		System.out.println("***********************SUBNET " + (char)charIndex + "*************************");
		System.out.println("Block Size..................: " + (int)Math.pow(2, 32-mask));
		System.out.println("Number of IPs Alloted.......: " + numberOfSubnetIPs);
		System.out.println("Subnet Mask.................: " + getDecimalMask());
		System.out.println("First IP Address............: " + firstIP);
		System.out.println("Last IP Address.............: " + lastIP);
		System.out.println("Range of IPs Alloted........: " + getAllotedRange(numberOfSubnetIPs));
		System.out.println("Range of IPs Available......: " + getAvailableRange(numberOfSubnetIPs));
		System.out.println();
	}

	public static boolean validateIP(String inputIP) {
		String partRegex = "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";
		String fullRegex = partRegex+"[.]"+partRegex+"[.]"+partRegex+"[.]"+partRegex;
		Pattern ipPattern = Pattern.compile(fullRegex);
		if(inputIP == null) {
			return false;
		} else {
			Matcher ipMatcher = ipPattern.matcher(inputIP);
			return ipMatcher.matches();
		}
	}

	public String[] decimalToBinary(String inputIP) {
		String[] decimalValues = inputIP.split("[.]");
		String[] binaryValues = new String[4];
		int index = 0;
		for (String decimalValue : decimalValues) {
			binaryValues[index] = toEightBitBinary(Integer.toBinaryString(Integer.parseInt(decimalValue)));
			index++;
		}
		return binaryValues;
	}
	
	public String toEightBitBinary(String binary) {
		int offset = 8 - binary.length();
		String temp = "";
		for(int i=1; i<=offset; i++) {
			temp += "0";	
		}
		return temp + binary;
	}

	public String[] getSubnetMask(int mask) {
		String subnetMask = "";
		for (int i=1;i<=32;i++) {
			if(i<=mask) {
				if(i%8==0) {
					subnetMask += "1.";
				} else {
					subnetMask += "1";
				}
			} else {
				if(i%8==0) {
					subnetMask += "0.";
				} else {
					subnetMask += "0";
				}
			}
	 	}
	 	return subnetMask.split("[.]"); 
	}

	public String getDecimalMask() {
		String ip = "";
		for(String binaryString : subnetMask) {
			ip += Integer.toString(Integer.parseInt(binaryString, 2)) + ".";
		}
		return ip.substring(0, ip.length()-1);
	}

	public void getNetworkClass() {
		String firstByte = binaryIP[0]; 
		if(firstByte.charAt(0) == '0') {
			System.out.println("Class of IP Address ........: A");
		} else {
			if(firstByte.charAt(1) == '0') {
				System.out.println("Class of IP Address ........: B");
			} else {
				if(firstByte.charAt(2) == '0') {
					System.out.println("Class of IP Address ........: C");
				} else {
					if(firstByte.charAt(3) == '1') {
						System.out.println("Class of IP Address ........: D");
					} else {
						System.out.println("Class of IP Address ........: E");
					}
				}
			}
		}
	}

	public String getFirstIP(String[] inputIP, String[] inputSubnetMask) {
		String startIp = "";
		for(int i=0; i<4;i++) {
			int val1 = Integer.parseInt(inputIP[i],2);
			int val2 = Integer.parseInt(inputSubnetMask[i],2);
			startIp += (val1&val2) + ".";
		}
		startIp = startIp.substring(0, startIp.length()-1);
		return startIp;
	}

	public String getLastIP(String[] inputIP, String[] inputSubnetMask) {
		String endIp = "";
		for(int i=0; i<4;i++) {
			int val1 = Integer.parseInt(inputIP[i], 2); 
			int val2 = Integer.parseInt(inputSubnetMask[i], 2);
			endIp += (256 + (val1|(~val2))) + ".";
		}
		endIp = endIp.substring(0, endIp.length()-1);
		return endIp;
	}

	public static String generateNewIP(String inputIP) {
		String[] splitIP = inputIP.split("[.]");
		if(splitIP[3].equals("255")) {
			splitIP[3] = "0";
			if(splitIP[2].equals("255")) {
				splitIP[2] = "0";
				if(splitIP[1].equals("255")) {
					splitIP[1] = "0";
					if(splitIP[0].equals("255")) {
						splitIP[0] = "0";
						System.err.println("IPs Exhausted");
					} else {
						splitIP[0] = Integer.toString(Integer.parseInt(splitIP[0])+1);
					}
				} else {
					splitIP[1] = Integer.toString(Integer.parseInt(splitIP[1])+1);
				}
			} else {
				splitIP[2] = Integer.toString(Integer.parseInt(splitIP[2])+1);	
			}
		} else {
			splitIP[3] = Integer.toString(Integer.parseInt(splitIP[3])+1);
		}
		return splitIP[0] + "." + splitIP[1] + "." + splitIP[2] + "." + splitIP[3];
	}

	public String getAllotedRange(int requestedIPs) {
		int permissibleIPs = (int)Math.pow(2, 32-mask); 
		if(requestedIPs == permissibleIPs) {
			return firstIP + " - " + lastIP;
		} else if(requestedIPs < permissibleIPs) {
			return firstIP + " - " + calulateLastIP(requestedIPs-1);
		} else {
			return null;
		}
	}

	public String getAvailableRange(int requestedIPs) {
		int permissibleIPs = (int)Math.pow(2, 32-mask); 
		if(requestedIPs == permissibleIPs) {
			return "All Requested IPs Alloted";
		} else if(requestedIPs < permissibleIPs) {
			return calulateLastIP(requestedIPs) + " - " + lastIP;
		} else {
			return null;
		}
	}

	public String calulateLastIP(int num) {
		String[] splitIP = firstIP.split("[.]");
		splitIP[3] = Integer.toString(Integer.parseInt(splitIP[3])+num);
		while(Integer.parseInt(splitIP[3]) > 255) {
			splitIP[3] = Integer.toString(Integer.parseInt(splitIP[3])-255);
			splitIP[2] = Integer.toString(Integer.parseInt(splitIP[2])+1);
			while(Integer.parseInt(splitIP[2]) > 255) {
				splitIP[2] = Integer.toString(Integer.parseInt(splitIP[2])-255);
				splitIP[1] = Integer.toString(Integer.parseInt(splitIP[1])+1);
				while(Integer.parseInt(splitIP[1]) > 255) {
					splitIP[1] = Integer.toString(Integer.parseInt(splitIP[1])-255);
					splitIP[0] = Integer.toString(Integer.parseInt(splitIP[0])+1);
					if(Integer.parseInt(splitIP[0]) > 255) {
						return "";
					}
				}
			}
		}
		return splitIP[0] + "." + splitIP[1] + "." + splitIP[2] + "." + splitIP[3];
	}
}