package pe.seek.core.shared.common;

public interface CopyEntity<ENTITY> {
    ENTITY copyFrom(ENTITY item);
}
