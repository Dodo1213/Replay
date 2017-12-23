package net.giantgames.replay.util;

import com.google.common.reflect.ClassPath;
import net.giantgames.replay.ReplayPlugin;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * The Classes Utility presents a lot of useful methods for the work with multiple classes or packages.
 * <p>
 * Reflection is used in almost every method so you might wan't to call some of them async, but a view methods should
 * be called sync because of the ClassPath.
 *
 * @author Merlin
 * @since 1.0
 */

public class Classes {
    /**
     * Scans the packet for all classes of the given type and creates a new instance using the {@link Class#newInstance()}
     * method. If the packet contains classes that don't have a default constructor, an exception will be thrown.
     * When a new instance is created the consumer will be executed.
     *
     * @param url      root package url
     * @param type     type of classes
     * @param consumer consumer that takes all instances
     * @param <E>      generic type
     */
    public static <E> void forEach(String url, Class<E> type, Consumer<E> consumer) {
        forEach(url, type, consumer, new String[0]);
    }

    /**
     * Scans the packet for all classes of the given type and creates a new instance using the {@link Class#newInstance()}
     * method. If the packet contains classes that don't have a default constructor, an exception will be thrown.
     * When a new instance is created the consumer will be executed.
     *
     * @param url      root package url
     * @param type     type of classes
     * @param consumer consumer that takes all instances
     * @param subPaths sub packages
     * @param <E>      generic type
     */
    public static <E> void forEach(String url, Class<E> type,
                                   Consumer<E> consumer, String[] subPaths) {
        forEach(url, type, consumer, subPaths, true);
    }

    /**
     * Scans the packet for all classes of the given type and creates a new instance using the {@link Class#newInstance()}
     * method. If the packet contains classes that don't have a default constructor, an exception will be thrown.
     * When a new instance is created the consumer will be executed.
     *
     * @param url         root package url
     * @param type        type of classes
     * @param consumer    consumer that takes all instances
     * @param subPaths    sub packages
     * @param executeRoot should the consumer accept classes from the root package or only sub packages?
     * @param <E>         generic type
     */
    public static <E> void forEach(String url, Class<E> type,
                                   Consumer<E> consumer, String[] subPaths, boolean executeRoot) {

        try {
            ClassPath classPath = ClassPath.from(ReplayPlugin.class.getClassLoader());

            if (executeRoot) {
                for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(url)) {
                    Class<?> clazz = classInfo.load();

                    if (!type.isAssignableFrom(clazz)) {
                        continue;
                    }

                    consumer.accept((E) clazz.newInstance());
                }
            }

            for (int i = 0; i < subPaths.length; i++) {
                for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(url.concat(".".concat(subPaths[i])))) {
                    Class<?> clazz = classInfo.load();
                    if (!type.isAssignableFrom(clazz)) {
                        continue;
                    }

                    consumer.accept((E) clazz.newInstance());
                }
            }

        } catch (IOException | InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

}
