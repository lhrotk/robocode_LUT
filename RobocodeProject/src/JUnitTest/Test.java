package JUnitTest;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;

import com.lhrotk.lut.StateAction;

public class Test {

	@Before
	public void setUp() throws Exception {
	}

	@org.junit.Test
	public void testHashCode() {
		HashMap<StateAction , Integer> map = new HashMap<StateAction, Integer>();
		map.put(new StateAction(new int[] {1,2,3,4,5}), 1);
		System.out.println(new StateAction(new int[] {1,2,3,4,5}).hashCode());
		System.out.println(new StateAction(new int[] {1,2,3,4,5}).hashCode());
		System.out.println(Arrays.toString(new Integer[] {1,2,3,4,5}).hashCode());
		System.out.println(Arrays.toString(new Integer[] {1,2,3,4,5}).hashCode());
		assertEquals(1, (int)map.get(new StateAction(new int[] {1,2,3,4,5})));
	}

	@org.junit.Test
	public void testEqualsObject() {
		assertEquals(new StateAction(new int[] {1,2,3,4,5}),new StateAction(new int[] {1,2,3,4,5}));
	}

}
