package me.gaigeshen.mybatis.helper;

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

  private boolean hasPrevious;

  private boolean hasNext;
  
  private boolean first;
  private boolean last;
  
  public PageData(List<T> content, int page, int size, long total) {
    this.content = content;
    this.page = page;
    this.size = size;
    this.total = total;
    
    long pages = total / size;
    if (total % size > 0) {
      pages++;
    }
    this.pages = pages;
    
    this.hasPrevious = page > 1;
    this.hasNext = this.pages > page;
    
    this.first = !this.hasPrevious;
    this.last = !this.hasNext;
  }

  /**
   * Use this method, translate objects to another type objects
   *
   * @param fun Translator function
   * @param <S> Target object class
   * @return New page data translated
   */
  public <S> PageData<S> map(Function<T, S> fun) {
    
    List<S> otherContent = null;
    
    if (content != null) {
      otherContent = content.stream()
        .map(fun)
        .collect(Collectors.toList());
    }
    
    return new PageData<>(otherContent, page, size, total);
  }
  
  public List<T> getContent() { return content; }

  public int getPage() { return page; }

  public int getSize() { return size; }

  public long getPages() { return pages; }

  public long getTotal() { return total; }

  public boolean isFirst() { return first; }

  public boolean isLast() { return last; }

}
