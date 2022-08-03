package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.mypage.Update;
import cn.pivotstudio.modulec.homescreen.oldversion.mypage.UpdateRecycleViewAdapter;
import com.githang.statusbar.StatusBarCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    List<Update> updates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ImageView img = findViewById(R.id.update_img);
        img.setOnClickListener(v -> finish());
        init();
        RecyclerView updateRecycleView = (RecyclerView) findViewById(R.id.update_recyclerview);
        updateRecycleView.setLayoutManager(new LinearLayoutManager(this));
        UpdateRecycleViewAdapter adapter = new UpdateRecycleViewAdapter(updates);
        updateRecycleView.setAdapter(adapter);
    }

    private void init() {
        for (int i = 0; i < 10; i++) {
            Update _1_0_1 = new Update("v 1.0", "2021-09-21", " 1037树洞是一个华科校内匿名社区，您不用担心被熟悉的人发现身份.\n"
                + " -身份验证：允许在注册后通过华科校内邮箱来验证在校学生身份；\n"
                + "-树洞发布：可匿名发布文字内容到所有人都能看到的树洞广场\n"
                + "-树洞搜索：支持洞号、关键词搜索，方便您找到有趣的树洞；\n"
                + "-树洞交流：您可以对感兴趣的树洞进行点赞、评论、关注的操作，您的树洞在被评论后会收到通知；\n"
                + "-小树林：聚合同类型树洞，方便您浏览感兴趣话题；\n"
                + "-关键词屏蔽：对您不感兴趣的树洞内容，支持自定义设置关键词进行屏蔽；\n"
                + "-只看洞主：浏览树洞内容时，您可以选择只看洞主发布的评论；\n"
                + "-热门评论：浏览树洞内容时，您可以查看树洞下的最热评论；\n"
                + "-我的：支持对我的树洞、我的关注、我的评论进行统一管理，您可以保存图片分享树洞给好友。");
            /*
            Update _1_0_0 = new Update("v 1.0","2021-03-08","新增功能：\n" +
                    "- 限制未验证校园邮箱的发布树洞和评论权限；\n" +
                    "- 关键词搜索；\n" +
                    "体验优化：\n" +
                    "- 点击树洞评论昵称时跳转的评论会闪动提醒。");
            Update _0_3_0 = new Update("Beta 0.3.0","2021-02-20","新增功能：\n" +
                    "- 聚合同类型树洞的小树林模块。");
            Update _0_2_3 = new Update("Beta 0.2.3","2021-02-20","新增功能：\n" +
                    "- 对于树洞和评论的删除功能；\n" +
                    "- 树洞评论可以按照发布时间正序或倒序进行排序；\n" +
                    "体验优化：\n" +
                    "- 优化了注册流程；\n" +
                    "- 优化了树洞评论页加载体验；\n" +
                    "- 首页上拉加载体验优化。");
            Update _0_2_2 = new Update("Beta 0.2.2","2020-12-31","新增功能：\n" +
                    "- 对树洞评论的回复功能，回复后点击绿色评论昵称能够跳转至被回复的树洞评论；\n" +
                    "体验优化：\n" +
                    "- 修改了树洞内容行数为一行、两行时的树洞框高度；\n" +
                    "- 优化了首页树洞的点击体验，现在按下时会改变背景颜色；");
            Update _0_2_1 = new Update("Beta 0.2.1","2020-12-30","体验优化：\n" +
                    "-  首页刷新功能优化，现在页面在顶部的时候点击一次首页就能刷新页面，并且有动画提醒；\n" +
                    "-  优化了评论输入框的设计；\n" +
                    "-  当评论字数接近以及超过255字限制时，会给出提醒；\n" +
                    "-  发布树洞字数限制改为15字；\n" +
                    "BUG修复：\n" +
                    "-  修复了“评论洞主：”随着评论页上滑的问题。");
            Update _0_2_0 = new Update("Beta 0.2.0","2020-12-28","新增功能：\n" +
                    "-  树洞广场新增按最新评论时间排序；\n" +
                    "-  校园邮箱验证页新增校园邮箱使用指引； \n" +
                    "-  发布树洞最少字数20字；\n" +
                    "体验优化：\n" +
                    "-  关注树洞Icon调整；\n" +
                    "BUG修复：\n" +
                    "-  修复了重复发布树洞的问题。");
            Update _0_1_0 = new Update("Beta 0.1.0","2020-12-12","产品内测，欢迎体验:)\n" +
                    "1037树洞是一个华科校内匿名社区，您不用担心被熟悉的人发现身份。\n" +
                    "-  身份验证：允许在注册后通过华科校内邮箱来验证在校学生身份；\n" +
                    "-  树洞发布：可匿名发布文字内容到所有人都能看到的树洞广场；\n" +
                    "-  树洞搜索：支持通过洞号进行搜索，方便您找到有趣的树洞；\n" +
                    "-  树洞交流：您可以对感兴趣的树洞进行点赞、评论、关注的操作，您的树洞在被评论后会收到通知；\n" +
                    "-  我的：支持对自己发布的树洞（我的树洞）和正在关注的树洞（我的关注）进行统一管理。");

             */
            updates.clear();
            updates.addAll(Arrays.asList(_1_0_1));
            //updates.addAll(Arrays.asList(_1_0_0, _0_3_0, _0_2_3, _0_2_2, _0_2_1, _0_2_0, _0_1_0));
        }
    }
}
