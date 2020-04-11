package top.lexiang.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lexiang.common.entity.Result;
import top.lexiang.common.utils.IdWorker;
import top.lexiang.notice.client.ArticleClient;
import top.lexiang.notice.client.SystemClient;
import top.lexiang.notice.entity.Notice;
import top.lexiang.notice.entity.NoticeFresh;
import top.lexiang.notice.mapper.NoticeFreshMapper;
import top.lexiang.notice.mapper.NoticeMapper;
import top.lexiang.notice.service.NoticeService;

import java.util.HashMap;
import java.util.List;


@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeFreshMapper noticeFreshMapper;

    @Autowired
    private ArticleClient articleClient;

    @Autowired
    private SystemClient systemClient;


    @Override
    public Notice selectById(String id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public int delete(String id) {
        return noticeMapper.deleteById(id);
    }

    @Override
    public int freshDelete(NoticeFresh noticeFresh) {
        return noticeFreshMapper.delete(new QueryWrapper<>(noticeFresh));
    }

    @Override
    public Page<NoticeFresh> freshPage(String userId, Integer page, Integer size) {
        //封装查询条件
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setUserId(userId);

        //创建分页对象
        Page<NoticeFresh> pageData = new Page<>(page, size);
        Page<NoticeFresh> noticeFreshPage = noticeFreshMapper.selectPage(pageData, new QueryWrapper<>(noticeFresh));
        return noticeFreshPage;
    }

    @Override
    public int updateById(Notice notice) {
        return noticeMapper.updateById(notice);
    }

    @Override
    @Transactional
    public void save(Notice notice) {
        //分布式id生成器
        String id = idWorker.nextId() + "";
        notice.setId(id);
        //TODO 设置进行操作用户的id  获取用户的角色 设置消息是系统消息（system）还是用户消息（uesr)
        notice.setOperatorId("1"); //操作者 A对B进行点赞 该参数是设置A的值
        // 保存消息内容
        noticeMapper.insert(notice);

        /* 已被rabbitmq替换
        //待推送消息入库，新消息提醒
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setNoticeId(id); //消息id
        noticeFresh.setUserId(notice.getReceiverId());//待通知用户的id
        noticeFreshMapper.insert(noticeFresh);
         */
    }


    /**
     * 查询消息相关数据
     */
    private void getNoticeInfo(Notice notice) {
        //获取用户信息
        Result userResult = systemClient.findById(notice.getOperatorId());
        HashMap userMap = (HashMap) userResult.getData();
        if (userMap.get("nickName") != null) {
            notice.setOperatorName(userMap.get("nickName").toString());
        }

        //获取文章信息
        if ("article".equals(notice.getTargetType())) {
            Result articleResult = articleClient.findByProblemId(notice.getTargetId());
            HashMap articleMap = (HashMap) articleResult.getData();
            notice.setTargetName(articleMap.get("title").toString());
        }
    }

    @Override
    public Page<Notice> selectByPage(Notice notice, Integer page, Integer size) {

        Page<Notice> pageData = new Page<>(page, size);
        QueryWrapper queryWrapper = new QueryWrapper(notice);
        Page r_page = noticeMapper.selectPage(pageData, queryWrapper);//返回的信息就有操作对象的id，通过feign获取其昵称等信息

        List<Notice> list = r_page.getRecords();

        for (Notice n : list) {
            getNoticeInfo(n);
        }

        pageData.setRecords(list);
        return pageData;

    }

}
