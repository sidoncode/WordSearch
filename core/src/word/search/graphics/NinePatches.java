package word.search.graphics;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import word.search.managers.ResourceManager;

public class NinePatches {

    public static NinePatch board_bg;
    public static NinePatch selection_3x3;
    public static NinePatch selection_4x4;
    public static NinePatch selection_5x5;
    public static NinePatch selection_6x6;
    public static NinePatch selection_7x7;
    public static NinePatch selection_8x8;

    public static NinePatch sel_anim_3x3;
    public static NinePatch sel_anim_4x4;
    public static NinePatch sel_anim_5x5;
    public static NinePatch sel_anim_6x6;
    public static NinePatch sel_anim_7x7;
    public static NinePatch sel_anim_8x8;

    public static NinePatch preview;
    public static NinePatch word_bg;
    public static NinePatch rect;
    public static NinePatch dialog;
    public static NinePatch dialog_title;
    public static NinePatch rrect;
    public static NinePatch word_cat_ribbon;
    public static NinePatch btn_green_large, btn_orange_large;
    public static NinePatch round_rect_shadow;
    public static NinePatch iap_card, iap_card_half, iap_top, iap_card_title, iap_content;
    public static NinePatch feedback_ribbon;


    public static void init(ResourceManager resourceManager) {

        TextureAtlas atlas1 = resourceManager.get(ResourceManager.ATLAS_1, TextureAtlas.class);

        board_bg            = atlas1.createPatch("board_bg");
        selection_3x3       = atlas1.createPatch("selection_3x3");
        selection_4x4       = atlas1.createPatch("selection_4x4");
        selection_5x5       = atlas1.createPatch("selection_5x5");
        selection_6x6       = atlas1.createPatch("selection_6x6");
        selection_7x7       = atlas1.createPatch("selection_7x7");
        selection_8x8       = atlas1.createPatch("selection_8x8");

        sel_anim_3x3        = atlas1.createPatch("sel_anim_3x3");
        sel_anim_4x4        = atlas1.createPatch("sel_anim_4x4");
        sel_anim_5x5        = atlas1.createPatch("sel_anim_5x5");
        sel_anim_6x6        = atlas1.createPatch("sel_anim_6x6");
        sel_anim_7x7        = atlas1.createPatch("sel_anim_7x7");
        sel_anim_8x8        = atlas1.createPatch("sel_anim_8x8");

        preview             = atlas1.createPatch("preview");
        word_bg             = atlas1.createPatch("word_bg");
        rect                = atlas1.createPatch("rect");
        dialog              = atlas1.createPatch("dialog");
        dialog_title        = atlas1.createPatch("dialog_title");
        rrect               = atlas1.createPatch("rrect");
        word_cat_ribbon     = atlas1.createPatch("word_cat_ribbon");
        btn_green_large     = atlas1.createPatch("btn_green_large");
        btn_orange_large    = atlas1.createPatch("btn_orange_large");
        round_rect_shadow   = atlas1.createPatch("round_rect_shadow");
        iap_card            = atlas1.createPatch("iap_card");
        iap_card_half       = atlas1.createPatch("iap_card_half");
        iap_top             = atlas1.createPatch("iap_top");
        iap_card_title      = atlas1.createPatch("iap_card_title");
        iap_content         = atlas1.createPatch("iap_content");
        feedback_ribbon     = atlas1.createPatch("feedback_ribbon");
    }
}
