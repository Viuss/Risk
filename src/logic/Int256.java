package logic;

public class Int256 {
    public final int[] value;

    public Int256(int[] value) {
        if(value.length != 8) {
            throw new IllegalArgumentException("value must be int array of length 8: 256bit");
        }

        this.value = value;
    }

    public Int256 xor(Int256 operand) {
        throw new RuntimeException("Not implemented");
    }
}
