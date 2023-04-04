package org.example;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
    public class MultArrays {

        private static final String KERNEL =
                "kernel void mul_arrays(global const float *a, global const float *b, global float *answer) {"
                        + "unsigned int xid = get_global_id(0); answer[xid] = a[xid] * b[xid]; }";

        private static final float[] LEFT_ARRAY = { 1F, 3F, 5F, 7F};
        private static final float[] RIGHT_ARRAY = { 2F, 4F, 6F, 8F};

        private static void printSequence(String label, FloatBuffer sequence, PrintStream to) {
            to.print(label);
            to.print(": [ ");
            for (int i = 0; i < sequence.limit(); i++) {
                to.print(' ');
                to.print(Float.toString(sequence.get(i)));
                to.print(' ');

            }
            to.println(" ]");
        }

        public static void main(String[] args) {
            try (ClRuntime cl = new ClRuntime(); MemoryStack stack = MemoryStack.stackPush();) {
                ClRuntime.Platform platform = cl.getPlatforms().first();
                ClRuntime.Device device = platform.getDefault();
                try (ClRuntime.Context context = device.createContext();
                     ClRuntime.Program program = context.createProgramWithSource(KERNEL)) {

                    FloatBuffer lhs = stack.floats(LEFT_ARRAY);
                    FloatBuffer rhs = stack.floats(RIGHT_ARRAY);

                    printSequence("Left hand statement: ", lhs, System.out);
                    printSequence("Right hand statement: ", rhs, System.out);

                    int gws = LEFT_ARRAY.length * Float.BYTES;

                    ClRuntime.CommandQueue cq = program.getCommandQueue();

                    final ClRuntime.VideoMemBuffer first = cq.hostPtrReadBuffer(MemoryUtil.memAddressSafe(lhs), gws);
                    final ClRuntime.VideoMemBuffer second = cq.hostPtrReadBuffer(MemoryUtil.memAddressSafe(rhs), gws);
                    final ClRuntime.VideoMemBuffer answer = cq.createReadWriteBuffer(gws);

                    cq.flush();

                    ClRuntime.Kernel sumVectors = program.createKernel("mul_arrays");
                    sumVectors.arg(first).arg(second).arg(answer).executeAsDataParallel(gws);

                    ByteBuffer result = MemoryUtil.memAlloc(answer.getCapacity());
                    cq.readVideoMemory(answer, result);

                    printSequence("Result: ", result.asFloatBuffer(), System.out);

                } catch (ExecutionException exc) {
                    System.err.println(exc.getMessage());
                    System.exit(-1);
                }
            }
        }

    }