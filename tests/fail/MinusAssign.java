package fail;

import java.lang.System;

public class MinusAssign {
	public static void main(String[] args) {
		char a = 'a';
		System.out.println(a -= 42); // only supports integer types
	}
}
