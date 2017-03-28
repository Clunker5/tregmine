package info.tregmine.database;

public interface IContext extends AutoCloseable {
    @Override
    void close();

    IBankDAO getBankDAO();

    IBlessedBlockDAO getBlessedBlockDAO();

    IBlockDAO getBlockDAO();

    IDiscordDAO getDiscordDAO();

    IEnchantmentDAO getEnchantmentDAO();

    IFishyBlockDAO getFishyBlockDAO();

    IHandbookDAO getHandbookDAO();

    IHomeDAO getHomeDAO();

    IInventoryDAO getInventoryDAO();

    IInviteDAO getInviteDAO();

    IItemDAO getItemDAO();

    ILogDAO getLogDAO();

    IMailDAO getMailDAO();

    IMentorLogDAO getMentorLogDAO();

    IMiscDAO getMiscDAO();

    IMotdDAO getMotdDAO();

    IStaffNewsDAO getNewsByUploader();

    IPlayerDAO getPlayerDAO();

    IPlayerReportDAO getPlayerReportDAO();

    ITradeDAO getTradeDAO();

    IWalletDAO getWalletDAO();

    IWarpDAO getWarpDAO();

    IZonesDAO getZonesDAO();
}
