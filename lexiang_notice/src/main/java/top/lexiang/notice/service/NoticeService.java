package top.lexiang.notice.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.lexiang.entity.notice.Notice;
import top.lexiang.entity.notice.NoticeFresh;

public interface NoticeService {

    /**
     * 根据id查询消息
     */
    Notice selectById(String id);

    /**
     * 删除消息
     */
    int delete(String id);

    /**
     * 删除待推送消息
     */
    int freshDelete(NoticeFresh noticeFresh);

    /**
     * 根据用户id查询该用户的待推送消息
     */
    Page<NoticeFresh> freshPage(String userId, Integer page, Integer size);

    /**
     * 修改通知
     */
    int updateById(Notice notice);

    /**
     * 新增通知
     */
    void save(Notice notice);

    /**
     * 根据条件分页查询消息通知
     */
    Page<Notice> selectByPage(Notice notice, Integer page, Integer size);


}
