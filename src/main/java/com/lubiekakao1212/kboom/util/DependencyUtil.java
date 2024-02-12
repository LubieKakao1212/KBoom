package com.lubiekakao1212.kboom.util;


import java.util.*;
import java.util.function.Function;

public class DependencyUtil {

    /**
     * @return order is reversed?
     */
    public static <T> List<T> dependencySort(Set<T> elements, Function<T, List<T>> dependencyGetter) {
        var tmpMarks = new HashSet<T>(elements.size());
        var permMarks = new HashSet<T>(elements.size());
        var result = new ArrayList<T>(elements.size());
        var elementList = new ArrayList<T>(elements);
        for(var element : elementList) {
            if(permMarks.contains(element)) {
                continue;
            }
            visit(result, elements, tmpMarks, permMarks, element, dependencyGetter);
        }
        return result;
    }

    /**
     *
     * @return null -> correct <br/> not null -> missing dependency
     */
    private static <T> T visit(List<T> result, Set<T> elements, Set<T> tmpMarks, Set<T> permMarks, T element, Function<T, List<T>> dependencyGetter) {
        if(!elements.contains(element)) {
            return element;
        }
        if(permMarks.contains(element)) {
            return null;
        }
        if(tmpMarks.contains(element)) {
            throw DependencyException.cycle(element);
        }

        tmpMarks.add(element);

        for(var e : dependencyGetter.apply(element)) {
            var missing = visit(result, elements, tmpMarks, permMarks, e, dependencyGetter);
            if(missing != null) {
                throw DependencyException.missing(element, missing);
            }
        }

        tmpMarks.remove(element);
        permMarks.add(element);
        result.add(element);

        return null;
    }

    public static class DependencyException extends RuntimeException {

        private DependencyException(String message) {
            super(message);
        }

        public static <T> DependencyException cycle(T element) {
            return new DependencyException("Dependency cycle detected involving: " + element);
        }

        public static <T> DependencyException missing(T element, T missingElement) {
            return new DependencyException("Element " + element + "is missing dependency: " + missingElement);
        }

    }

}
