package net.giantgames.replay.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.*;

public class ObjectSerializer {

    public static byte[] writeToBuf(Serializable serializable) {
        byte[] buf = new byte[0];

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;

        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            buf = byteArrayOutputStream.toByteArray();
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close(); // Wont do anything though
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return buf;
    }

    public static <E extends Serializable> E readFromBuf(byte[] buf) {
        Object object = new Object();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
        ObjectInputStream objectInputStream = null;

        try {
            object = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }

                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return (E) object;
    }

    public static void writeToFile(File file, Serializable serializable) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(serializable);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void writeToFile(File file, byte[] data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static <E extends Serializable> E readFromFile(File file) {
        Object object = new Object();

        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);

            object = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }

                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return (E) object;

    }

    public static byte[] readBufFromFile(File file) {
        byte[] buf = new byte[0];

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            buf = IOUtils.toByteArray(fileInputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return buf;
    }

    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream(data.length);

            Deflater deflater = new Deflater();
            deflater.setInput(data);
            deflater.finish();

            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                byteArrayOutputStream.write(buffer, 0, deflater.deflate(buffer));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decompress(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream(data.length);

            Inflater inflater = new Inflater();
            inflater.setInput(data);

            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                byteArrayOutputStream.write(buffer, 0, inflater.inflate(buffer));
            }

        } catch (DataFormatException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }


}
