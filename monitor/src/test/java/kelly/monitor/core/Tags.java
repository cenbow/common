package kelly.monitor.core;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

public class Tags {

    private final List<Tag> tags = Lists.newArrayListWithCapacity(8);

    public void addTag(String key, String value) {
        tags.add(Tag.newTag(key, value));
    }

    @Override
    public String toString() {
        return "Tags{" +
                "tags=" + tags +
                '}';
    }

    List<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tags tags1 = (Tags) o;

        return tagsEquals(tags1.tags, tags);
    }

    private boolean tagsEquals(List<Tag> leftTag, List<Tag> rightTag) {
        if (leftTag.size() != rightTag.size()) {
            return false;
        }
        if (leftTag.size() != 0 && rightTag.size() != 0) {
            int code = leftTag.get(0).hashcode ^ rightTag.get(0).hashcode;
            for (int i = 1; i < leftTag.size(); i++) {
                Tag left = leftTag.get(i);
                Tag right = rightTag.get(i);
                code = code ^ left.hashcode ^ right.hashcode;
            }
            return code == 0;
        }
        return true;

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tags);
    }


}

