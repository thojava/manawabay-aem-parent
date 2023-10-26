package nz.co.manawabay.core.models;

public interface SearchResults {

    String getId();

    String getHeader();

    String getQueryString();

    String getSearchRootPagePath();

    long getPageNumber();
}
