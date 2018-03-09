package kelly.monitor.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Created by kelly-lee on 2018/3/9.
 */
@Getter
@ToString
public class Paginator {

    private static final int pageWidth = 3;
    @NonNull
    private long count;//总数
    @NonNull
    private int pageIndex;//页号
    @NonNull
    private int pageSize;//每页显示数量
    private int pageNum; //页数
    private int start;
    private int end;

    public Paginator(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void init(long count, int pageSize) {
        this.count = Math.max(1, count);
        this.pageSize = Math.max(1, pageSize);
        this.pageNum = (int) (this.count / this.pageSize);
        this.pageNum += (this.count % this.pageSize == 0 ? 0 : 1);
        this.pageIndex = Math.max(1, Math.min(pageIndex, pageNum));
        this.start = Math.max(1, pageIndex - pageWidth);
        this.end = Math.min(pageIndex + pageWidth, pageNum);
    }

    public int getFirstResult() {
        return (pageIndex - 1) * pageSize;
    }

    public boolean hasFirst() {
        return pageIndex != 1;
    }

    public boolean hasPrev() {
        return pageIndex != 1;
    }

    public boolean hasNext() {
        return pageIndex != pageNum;
    }

    public boolean hasLast() {
        return pageIndex != pageNum;
    }
}
