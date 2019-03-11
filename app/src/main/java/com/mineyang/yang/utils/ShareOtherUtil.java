package com.mineyang.yang.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Description :分享到指定的应用
 * <p>
 * 作者：咕咚股东
 * 链接：https://www.jianshu.com/p/9522e24713e1
 * 來源：简书
 * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
 */

public class ShareOtherUtil {
    private static final String PACKAGE_WECHAT = "com.tencent.mm";               //微信
    private static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";      //qq聊天界面
    private static final String PACKAGE_QZONE = "com.qzone";                     //qq空间
    private static final String PACKAGE_SINA = "com.sina.weibo";                 //新浪微博
    private static final String AUTHORITY = "com.ume.browser.fileprovider";

    /**
     * 直接分享纯文本内容至QQ好友
     *
     * @param mContext
     * @param content
     */
    public static void shareQQ(Context mContext, String content) {
        if (isInstallApp(mContext, PACKAGE_WECHAT)) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, content);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "您需要安装QQ客户端", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 分享图片给QQ好友
     *
     * @param bitmap
     */
    public void shareImageToQQ(Bitmap bitmap, Context context) {
        if (isInstallApp(context, PACKAGE_MOBILE_QQ)) {
            try {
                Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(
                    context.getContentResolver(), bitmap, null, null));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("image/*");
                // 遍历所有支持发送图片的应用。找到需要的应用
                ComponentName componentName = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");

                shareIntent.setComponent(componentName);
                // mContext.startActivity(shareIntent);
                context.startActivity(Intent.createChooser(shareIntent, "Share"));
            } catch (Exception e) {
                //            ContextUtil.getInstance().showToastMsg("分享图片到**失败");
            }
        }
    }

    /**
     * 分享到qq空间
     *
     * @param mContext
     * @param photoPath
     */
    public static void shareImageToQQZone(Context mContext, String photoPath) {
        if (isInstallApp(mContext, PACKAGE_QZONE)) {
            //            photoPath = Environment.getExternalStorageDirectory() + "/UmeWeb/Bitmap/1.png";
            File file = new File(photoPath);
            if (!file.exists()) {
                String tip = "文件不存在";
                Toast.makeText(mContext, tip + " path = " + photoPath, Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent();
            //            ComponentName componentName = new ComponentName("com.tencent.mobileqq","cooperation.qzone.QzonePublishMoodProxyActivity");// 无用代码
            ComponentName componentName = new ComponentName("com.qzone", "com.qzonex.module.operation.ui.QZonePublishMoodActivity");
            intent.setComponent(componentName);
            intent.setAction("android.intent.action.SEND");
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_TEXT, "I'm so tired!!");//  分享文本
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));// 分享图片
            mContext.startActivity(intent);
        }
    }

    /**
     * 直接分享文本到微信好友
     *
     * @param context 上下文
     */
    public static void shareWechatFriend(Context context, String content) {
        if (isInstallApp(context, PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("android.intent.extra.TEXT", content);
            //            intent.putExtra("sms_body", content);
            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 直接分享图片到微信好友
     *
     * @param context
     * @param picFile
     */
    public static void shareWechatFriend(Context context, String content, File picFile) {
        if (isInstallApp(context, PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (picFile != null) {
                if (picFile.isFile() && picFile.exists()) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(context, AUTHORITY, picFile);
                    } else {
                        uri = Uri.fromFile(picFile);
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    //                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri);
                }
            }
            //            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startActivity(intent);
            context.startActivity(Intent.createChooser(intent, "Share"));
        } else {
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 直接分享文本和图片到微信朋友圈
     *
     * @param context
     * @param content
     */
    public static void shareWechatMoment(Context context, String content, File picFile) {
        if (isInstallApp(context, PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            //分享精确到微信的页面，朋友圈页面，或者选择好友分享页面
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            //            intent.setAction(Intent.ACTION_SEND_MULTIPLE);// 分享多张图片时使用
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            //添加Uri图片地址--用于添加多张图片
            //ArrayList<Uri> imageUris = new ArrayList<>();
            //intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            if (picFile != null) {
                if (picFile.isFile() && picFile.exists()) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(context, AUTHORITY, picFile);
                    } else {
                        uri = Uri.fromFile(picFile);
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
            }
            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }

    public static void shareToSinaFriends(Context context, String photoPath) {
        if (! isInstallApp(context, PACKAGE_SINA)) {
            Toast.makeText(context, "新浪微博没有安装！", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(photoPath);
        if (!file.exists()) {
            String tip = "文件不存在";
            Toast.makeText(context, tip + " path = " + photoPath, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        // 使用以下两种type有一定的区别，"text/plain"分享给指定的粉丝或好友 ；"image/*"分享到微博内容,下面这两个设置type的代码必须写在查询语句前面，否则找不到带有分享功能的应用。
        //        intent.setType("text/plain");
        intent.setType("image/*");// 分享文本|文本+图片|图片 到微博内容时使用
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> matchs = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        ResolveInfo resolveInfo = null;
        for (ResolveInfo each : matchs) {
            String pkgName = each.activityInfo.applicationInfo.packageName;
            if ("com.sina.weibo".equals(pkgName)) {
                resolveInfo = each;
                break;
            }
        }
        intent.setClassName(PACKAGE_SINA, resolveInfo.activityInfo.name);// 这里在使用resolveInfo的时候需要做判空处理防止crash
        intent.putExtra(Intent.EXTRA_TEXT, "Test Text String !!");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);
    }

    // 判断是否安装指定app
    private static boolean isInstallApp(Context context, String app_package) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }
}
