package org.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// POJO for deserializing the product-types.json
// Uses Jackson annotations for optional fields (bannerCategories, allSizes)
// Assumes Lombok for brevity (add @Data, @NoArgsConstructor if using); otherwise, add getters/setters manually
public class ProductType {
    private String name;
    private String startFrom;
    private String categoryUrl;
    private String searchTerm;
    private String expectedSizeType;
    private List<String> browseByList;
    private List<String> urlForBrowseByList;
    private List<String> filterList;
    @JsonProperty("bannerCategories")
    private List<String> bannerCategories = List.of(); // Default empty if absent
    @JsonProperty("noSizeFlag")
    private boolean noSizeFlag;
    @JsonProperty("allSizes")
    private List<String> allSizes = List.of(); // Default empty if absent

    // Default constructor (required for Jackson)
    public ProductType() {}

    // Getters and Setters (or use Lombok @Data)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartFrom() { return startFrom; }
    public void setStartFrom(String startFrom) { this.startFrom = startFrom; }

    public String getCategoryUrl() { return categoryUrl; }
    public void setCategoryUrl(String categoryUrl) { this.categoryUrl = categoryUrl; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public String getExpectedSizeType() { return expectedSizeType; }
    public void setExpectedSizeType(String expectedSizeType) { this.expectedSizeType = expectedSizeType; }

    public List<String> getBrowseByList() { return browseByList; }
    public void setBrowseByList(List<String> browseByList) { this.browseByList = browseByList; }

    public List<String> getUrlForBrowseByList() { return urlForBrowseByList; }
    public void setUrlForBrowseByList(List<String> urlForBrowseByList) { this.urlForBrowseByList = urlForBrowseByList; }

    public List<String> getFilterList() { return filterList; }
    public void setFilterList(List<String> filterList) { this.filterList = filterList; }

    public List<String> getBannerCategories() { return bannerCategories; }
    public void setBannerCategories(List<String> bannerCategories) { this.bannerCategories = bannerCategories; }

    public boolean isNoSizeFlag() { return noSizeFlag; }
    public void setNoSizeFlag(boolean noSizeFlag) { this.noSizeFlag = noSizeFlag; }

    public List<String> getAllSizes() { return allSizes; }
    public void setAllSizes(List<String> allSizes) { this.allSizes = allSizes; }

    @Override
    public String toString() {
        return "ProductType{" +
                "name='" + name + '\'' +
                ", startFrom='" + startFrom + '\'' +
                ", categoryUrl='" + categoryUrl + '\'' +
                ", searchTerm='" + searchTerm + '\'' +
                ", expectedSizeType='" + expectedSizeType + '\'' +
                ", browseByList=" + browseByList +
                ", urlForBrowseByList=" + urlForBrowseByList +
                ", filterList=" + filterList +
                ", bannerCategories=" + bannerCategories +
                ", noSizeFlag=" + noSizeFlag +
                ", allSizes=" + allSizes +
                '}';
    }
}