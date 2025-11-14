package com.delivery_api.Projeto.Delivery.API.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {
    private List<T> content;
    private PageInfo page;
    private Links links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Links {
        private String first;
        private String last;
        private String next;
        private String previous;
    }

    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements, String baseUrl) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        PageInfo pageInfo = new PageInfo();
        pageInfo.setNumber(page);
        pageInfo.setSize(size);
        pageInfo.setTotalElements(totalElements);
        pageInfo.setTotalPages(totalPages);
        pageInfo.setFirst(page == 0);
        pageInfo.setLast(page >= totalPages - 1);

        Links links = new Links();
        links.setFirst(baseUrl + "?page=0&size=" + size);
        links.setLast(baseUrl + "?page=" + (totalPages - 1) + "&size=" + size);
        if (page < totalPages - 1) {
            links.setNext(baseUrl + "?page=" + (page + 1) + "&size=" + size);
        }
        if (page > 0) {
            links.setPrevious(baseUrl + "?page=" + (page - 1) + "&size=" + size);
        }

        return new PagedResponse<>(content, pageInfo, links);
    }
}

