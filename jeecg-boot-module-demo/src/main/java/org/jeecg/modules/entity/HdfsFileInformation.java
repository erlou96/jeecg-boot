package org.jeecg.modules.entity;

import java.util.List;

public class HdfsFileInformation {

    private Long blockID;
    private String blockPoolId;
    private String blockName;
    private Long generationStamp;
    private List<String> hostName;
    private Long blockSize;


    public Long getBlockID() {
        return blockID;
    }

    public void setBlockID(Long blockID) {
        this.blockID = blockID;
    }

    public String getBlockPoolId() {
        return blockPoolId;
    }

    public void setBlockPoolId(String blockPoolId) {
        this.blockPoolId = blockPoolId;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public Long getGenerationStamp() {
        return generationStamp;
    }

    public void setGenerationStamp(Long generationStamp) {
        this.generationStamp = generationStamp;
    }

    public List<String> getHostName() {
        return hostName;
    }

    public void setHostName(List<String> hostName) {
        this.hostName = hostName;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }
}
