package recyclemug.ProjectMug.dto;

import lombok.Data;

import java.util.List;

@Data
public class MapPartnerInfoDto {
    private String businessName;
    private String address;
    private String nickname;
    private byte[] profilePicture;
    private String profilePictureAddress;
    private List<PartnerCupResponseDTO> partnerCups;

    public MapPartnerInfoDto(String businessName,String address,String nickname,byte[] profilePicture, String profilePictureAddress,List<PartnerCupResponseDTO> partnerCups){
        this.businessName = businessName;
        this.address = address;
        this.nickname = nickname;
        this.profilePicture = profilePicture;
        this.profilePictureAddress = profilePictureAddress;
        this.partnerCups = partnerCups;
    }
}
