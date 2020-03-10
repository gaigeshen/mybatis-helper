package me.gaigeshen.mybatis.helper;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The page data
 *
 * @author gaigeshen
 * @param <T> The entity type
 */
public final class PageData<T> {
  
  private List<T> content;
  
  private int page;
  private int size;
  
  private long pages;
  private long total;

  private boolean first;
  private boolean last;

  /**
   * Create page data
   *
   * @param content Data content, if null then create empty list internal
   * @param page Page index must be great than or equal 1
   * @param size Page size must be great than or equal 1
   * @param total Total count must be great than or equal 0
   * @param <E> The element class type of the content
   * @return The page data
   */
  public static <E> PageData<E> create(List<E> content, int page, int size, long total) {
    return new PageData<>(content, page, size, total);
  }

  /**
   * Create page data
   *
   * @param content Data content, if null then create empty list internal
   * @param page Page index must be great than or equal 1
   * @param size Page size must be great than or equal 1
   * @param total Total count must be great than or equal 0
   */
  public PageData(List<T> content, int page, int size, long total) {
    if (page < 1) {
      throw new IllegalArgumentException("The page must be great than or equal 1");
    }
    if (size < 1) {
      throw new IllegalArgumentException("The size must be great than or equal 1");
    }
    if (total < 0) {
      throw new IllegalArgumentException("The total must be great than or equal 0");
    }

    this.content = content != null ? content : Collections.emptyList();
    this.page = page;
    this.size = size;
    this.total = total;
    
    long pages = total / size;
    if (total % size > 0) {
      pages++;
    }
    this.pages = pages;

    this.first = !(page > 1);
    this.last = !(this.pages > page);
  }

  /**
   * Use this method, translate objects to another type objects
   *
   * @param fun Translator function
   * @param <S> Target object class
   * @return New page data translated
   */
  public <S> PageData<S> map(Function<T, S> fun) {
    if (fun == null) {
      throw new IllegalArgumentException("Function cannot be null");
    }
    List<S> otherContent = content.stream()
      .map(fun)
      .collect(Collectors.toList());
    return new PageData<>(otherContent, page, size, total);
  }

  /**
   * Returns true if the content is empty
   *
   * @return True if the content is empty
   */
  public boolean isEmpty() {
    return content.isEmpty();
  }

  /**
   * Returns data content
   *
   * @return The data content
   */
  public List<T> getContent() { return content; }

  /**
   * Returns page index
   *
   * @return The page index
   */
  public int getPage() { return page; }

  /**
   * Returns page size
   *
   * @return The page size
   */
  public int getSize() { return size; }

  /**
   * Returns total pages count
   *
   * @return The pages count
   */
  public long getPages() { return pages; }

  /**
   * Returns total count of data content
   *
   * @return The total count
   */
  public long getTotal() { return total; }

  /**
   * @return If this page data is first page, then returns {@code true}
   */
  public boolean isFirst() { return first; }

  /**
   * @return If this page data is last page, then returns {@code true}
   */
  public boolean isLast() { return last; }

}
