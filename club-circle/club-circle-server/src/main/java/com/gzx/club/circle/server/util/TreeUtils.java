package com.gzx.club.circle.server.util;

import com.gzx.club.circle.api.common.TreeNode;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TreeUtils {

    public static <T extends TreeNode> List<T> buildTree(List<T> nodes) {

        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }
        Map<Long, List<TreeNode>> groups = nodes.stream().collect(Collectors.groupingBy(TreeNode::getNodePId));
        // peek 主要用于调试、日志记录或其他类似的目的。peek 方法允许在流的每个元素上执行一个操作，但不会改变元素本身。
        return nodes.stream().filter(Objects::nonNull).peek(pnd -> {
            List<TreeNode> ts = groups.get(pnd.getNodeId());
            pnd.setChildren(ts);
        }).filter(TreeNode::getRootNode).collect(Collectors.toList());
    }

    public static <T extends TreeNode> void findAll(List<T> result, TreeNode node, Long targetId) {

        if (node.getNodeId().equals(targetId) || node.getNodePId().equals(targetId)) {
            addAll(result, node);
        } else {
            if (!CollectionUtils.isEmpty(node.getChildren())) {
                for (TreeNode child : node.getChildren()) {
                    findAll(result, child, targetId);
                }
            }
        }

    }


    private static <T extends TreeNode> void addAll(List<T> result, TreeNode node) {
        result.add((T) node);
        if (!CollectionUtils.isEmpty(node.getChildren())) {
            for (TreeNode child : node.getChildren()) {
                addAll(result, child);
            }
        }
    }

}
