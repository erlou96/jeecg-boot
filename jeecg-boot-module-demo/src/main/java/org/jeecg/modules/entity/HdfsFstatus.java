package org.jeecg.modules.entity;

public class HdfsFstatus {

    Boolean isFile;
    String path;
    String permission;
    String owner;
    String group;
    Long Size;
    Long lastModified;
    Short replication;
    Long BlockSize;
    String name;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getSize() {
        return Size;
    }

    public void setSize(Long size) {
        Size = size;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Short getReplication() {
        return replication;
    }

    public void setReplication(Short replication) {
        this.replication = replication;
    }

    public Long getBlockSize() {
        return BlockSize;
    }

    public void setBlockSize(Long blockSize) {
        BlockSize = blockSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HdfsFstatus(Boolean isFile, String path, String permission, String owner, String group, Long size, Long lastModified, Short replication, Long blockSize, String name) {
        this.isFile = isFile;
        this.path = path;
        this.permission = permission;
        this.owner = owner;
        this.group = group;
        Size = size;
        this.lastModified = lastModified;
        this.replication = replication;
        BlockSize = blockSize;
        this.name = name;
    }

    public HdfsFstatus(String path, String permission, String owner, String group, Long size, Long lastModified, Short replication, Long blockSize, String name) {
        this.path = path;
        this.permission = permission;
        this.owner = owner;
        this.group = group;
        Size = size;
        this.lastModified = lastModified;
        this.replication = replication;
        BlockSize = blockSize;
        this.name = name;
    }

    public Boolean getFile() {
        return isFile;
    }

    public void setFile(Boolean file) {
        isFile = file;
    }

    public HdfsFstatus() {

    }

    @Override
    public String toString() {
        return "HdfsFstatus{" +
                "isFile=" + isFile +
                ", path='" + path + '\'' +
                ", permission='" + permission + '\'' +
                ", owner='" + owner + '\'' +
                ", group='" + group + '\'' +
                ", Size=" + Size +
                ", lastModified=" + lastModified +
                ", replication=" + replication +
                ", BlockSize=" + BlockSize +
                ", name='" + name + '\'' +
                '}';
    }
}

